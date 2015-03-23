package edu.sjsu.cmpe273.lab2;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Server that manages startup/shutdown of a {@code ModeratorPoll} server.
 */
public class PollServiceServer {
  private static final Logger logger = Logger.getLogger(PollServiceServer.class.getName());

  /* The port on which the server should run */
  private int port = 50051;
  private ServerImpl server;

  private void start() throws Exception {
    server = NettyServerBuilder.forPort(port)
        .addService(PollServiceGrpc.bindService(new PollServiceImpl()))
        .build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        PollServiceServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws Exception {
    final PollServiceServer server = new PollServiceServer();
    server.start();
  }

  private class PollServiceImpl implements PollServiceGrpc.PollService {
    @Override
    public void createPoll(PollRequest req, StreamObserver<PollResponse> responseObserver) {
      logger.info("Server is creating a new poll for moderator id = " + req.getModeratorId());
      PollResponse reply = PollResponse.newBuilder().setId(RandomStringUtils.randomAlphanumeric(7)).build();
      responseObserver.onValue(reply);
      responseObserver.onCompleted();
    }
  }
}

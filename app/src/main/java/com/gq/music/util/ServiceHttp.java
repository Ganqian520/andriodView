package com.gq.music.util;

import java.io.InputStream;
import java.net.Socket;

import fi.iki.elonen.NanoHTTPD;

public class ServiceHttp extends NanoHTTPD {
  public ServiceHttp(int port) {
    super(port);
  }

  @Override
  public Response serve(IHTTPSession session) {
    System.out.println(session.getParameters());
    System.out.println("serve被调用");
    Response response = newFixedLengthResponse("Hello world");
    return response;
  }

  @Override
  protected ClientHandler createClientHandler(Socket finalAccept, InputStream inputStream) {
    return super.createClientHandler(finalAccept, inputStream);
  }
}

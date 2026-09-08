/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.incubator.semconv.db.internal;

import javax.annotation.Nullable;

/**
 * A database endpoint set rendered as an address and a port.
 *
 * <p>A target usually names the logical server a database client was configured to talk to,
 * rendered as {@code server.address} and {@code server.port}. Such a target stays the same across
 * routing, node selection, and retries, so it is derived from client configuration rather than from
 * the endpoint that served an individual operation.
 *
 * <p>An instrumentation that knows which endpoint served an individual operation renders that one
 * endpoint the same way and reports it as {@code network.peer.address} and {@code
 * network.peer.port}.
 *
 * <p>This class is internal and is hence not for public use. Its APIs are unstable and can change
 * at any time.
 */
public class DbServerTarget {

  private final String address;
  @Nullable private final Integer port;

  /**
   * Returns a builder for a target whose endpoints listen on {@code defaultPort} unless they are
   * configured otherwise.
   */
  public static DbServerTargetBuilder builder(int defaultPort) {
    return new DbServerTargetBuilder(defaultPort);
  }

  /**
   * Returns a target for an already-extracted Unix socket path, or {@code null} when the path is
   * invalid. Accepted paths are preserved verbatim and are not parsed as URIs or connection
   * strings.
   */
  @Nullable
  public static DbServerTarget unixSocket(@Nullable String path) {
    if (path == null
        || path.length() <= 1
        || path.charAt(0) != '/'
        || path.indexOf(',') >= 0
        || path.indexOf('=') >= 0
        || path.indexOf('%') >= 0
        || path.indexOf('@') >= 0
        || path.indexOf('?') >= 0
        || path.indexOf('#') >= 0) {
      return null;
    }
    return new DbServerTarget(path, null);
  }

  DbServerTarget(String address, @Nullable Integer port) {
    this.address = address;
    this.port = port;
  }

  /** Returns the value for {@code server.address}, or for {@code network.peer.address}. */
  public String getAddress() {
    return address;
  }

  /**
   * Returns the value for {@code server.port}, or for {@code network.peer.port}, or {@code null}
   * when the target listens on its default port or already carries its ports inside {@link
   * #getAddress()}.
   */
  @Nullable
  public Integer getPort() {
    return port;
  }
}

/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone;

import com.google.errorprone.scanner.BuiltInCheckerSuppliers;
import com.google.errorprone.scanner.ScannerSupplier;

import com.sun.tools.javac.main.Main.Result;
import com.sun.tools.javac.util.Context;

import java.io.PrintWriter;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

/**
 * An Error Prone compiler that matches the interface of {@link com.sun.tools.javac.main.Main}.
 *
 * <p>Unlike {@link BaseErrorProneCompiler}, it enables all built-in Error Prone checks by
 * default.
 *
 * <p>Used by plexus-java-compiler-errorprone.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ErrorProneCompiler {

  /**
   * Entry point for compiling Java code with error-prone enabled.
   * All default checks are run, and the compile fails if they find a bug.
   *
   * @param args the same args which could be passed to javac on the command line
   */
  public static void main(String[] args) {
    System.exit(compile(args).exitCode);
  }

  /**
   * Compiles in-process.
   *
   * @param listener listens to the diagnostics produced by error-prone
   * @param args the same args which would be passed to javac on the command line
   * @return result from the compiler invocation
   */
  public static Result compile(DiagnosticListener<JavaFileObject> listener, String[] args) {
    ErrorProneCompiler compiler =
        new ErrorProneCompiler.Builder().listenToDiagnostics(listener).build();
    return compiler.run(args);
  }

  /**
   * Programmatic interface to the error-prone Java compiler.
   *
   * @param args the same args which would be passed to javac on the command line
   * @return result from the compiler invocation
   */
  public static Result compile(String[] args) {
    return new Builder().build().run(args);
  }

  /**
   * Programmatic interface to the error-prone Java compiler.
   *
   * @param args the same args which would be passed to javac on the command line
   * @param out a {@link PrintWriter} to which to send diagnostic output
   * @return result from the compiler invocation
   */
  public static Result compile(String[] args, PrintWriter out) {
    ErrorProneCompiler compiler = new ErrorProneCompiler.Builder().redirectOutputTo(out).build();
    return compiler.run(args);
  }

  private final BaseErrorProneCompiler compiler;

  private ErrorProneCompiler(BaseErrorProneCompiler compiler) {
    this.compiler = compiler;
  }

  public static class Builder {
    private final BaseErrorProneCompiler.Builder builder =
        new BaseErrorProneCompiler.Builder().report(BuiltInCheckerSuppliers.defaultChecks());

    public ErrorProneCompiler build() {
      return new ErrorProneCompiler(builder.build());
    }

    public Builder named(String compilerName) {
      builder.named(compilerName);
      return this;
    }

    public Builder redirectOutputTo(PrintWriter errOutput) {
      builder.redirectOutputTo(errOutput);
      return this;
    }

    public Builder listenToDiagnostics(DiagnosticListener<? super JavaFileObject> listener) {
      builder.listenToDiagnostics(listener);
      return this;
    }

    public Builder report(ScannerSupplier scannerSupplier) {
      builder.report(scannerSupplier);
      return this;
    }
  }

  public Result run(String[] args) {
    return compiler.run(args);
  }

  public Result run(String[] argv, List<JavaFileObject> javaFileObjects) {
    return compiler.run(argv, javaFileObjects);
  }

  public Result run(
      String[] argv,
      Context context,
      JavaFileManager fileManager,
      List<JavaFileObject> javaFileObjects,
      Iterable<? extends Processor> processors) {
    return compiler.run(argv, context, fileManager, javaFileObjects, processors);
  }
}

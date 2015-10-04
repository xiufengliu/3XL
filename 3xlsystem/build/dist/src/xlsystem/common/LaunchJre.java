/*
 *
 * Copyright (c) 2011, Xiufeng Liu (xiliu@cs.aau.dk) and the eGovMon Consortium
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 */

package xlsystem.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LaunchJre {

    private static boolean isWindows() {
        String os = System.getProperty("os.name");
        if (os == null) {
                throw new IllegalStateException("os.name");
        }
        os = os.toLowerCase();
        return os.startsWith("windows");
    }

    public static File getJreExecutable() throws FileNotFoundException {
        String jreDirectory = System.getProperty("java.home");
        if (jreDirectory == null) {
                throw new IllegalStateException("java.home");
        }
        File exe;
        if (isWindows()) {
                exe = new File(jreDirectory, "bin/java.exe");
        } else {
                exe = new File(jreDirectory, "bin/java");
        }
        if (!exe.isFile()) {
                throw new FileNotFoundException(exe.toString());
        }
        return exe;
    }

    public static int launch(List<String> cmdarray) throws IOException,   InterruptedException {
    	byte[] buffer = new byte[1024];
        ProcessBuilder processBuilder = new ProcessBuilder(cmdarray);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        InputStream in = process.getInputStream();
        processBuilder.directory(new File("."));
        System.out.println(processBuilder.directory().getCanonicalPath());
        while (true) {
                int r = in.read(buffer);
                if (r <= 0) {
                        break;
                }
                System.out.write(buffer, 0, r);
        }
        return process.waitFor();
    }

    public static void main(String[] args) {
        try {
              //  Runtime.getRuntime().exec("ls");

                List<String> cmdarray = new ArrayList<String>();
                cmdarray.add(getJreExecutable().toString());
                cmdarray.add("-version");
                int retValue = launch(cmdarray);
                if (retValue != 0) {
                        System.err.println("Error code " + retValue);
                }
                System.out.println("OK");
        } catch (IOException e) {
                e.printStackTrace();
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
    }

}

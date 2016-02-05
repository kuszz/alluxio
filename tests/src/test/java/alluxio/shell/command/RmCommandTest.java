/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package alluxio.shell.command;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import alluxio.AlluxioURI;
import alluxio.exception.ExceptionMessage;
import alluxio.exception.AlluxioException;
import alluxio.shell.AbstractAlluxioShellTest;
import alluxio.shell.AlluxioShellUtilsTest;

/**
 * Tests for rm command.
 */
public class RmCommandTest extends AbstractAlluxioShellTest {
  @Test
  public void rmNotExistingDirTest() throws IOException {
    StringBuilder toCompare = new StringBuilder();
    mFsShell.run("mkdir", "/testFolder");
    toCompare.append(getCommandOutput(new String[] {"mkdir", "/testFolder"}));
    mFsShell.run("rm", "/testFolder");
    toCompare.append("rm: cannot remove a directory, please try rm -R <path>\n");
    Assert.assertEquals(toCompare.toString(), mOutput.toString());
  }

  @Test
  public void rmNotExistingFileTest() throws IOException {
    mFsShell.run("rm", "/testFile");
    String expected = ExceptionMessage.PATH_DOES_NOT_EXIST.getMessage("/testFile") + "\n";
    Assert.assertEquals(expected, mOutput.toString());
  }

  @Test
  public void rmTest() throws IOException {
    StringBuilder toCompare = new StringBuilder();
    mFsShell.run("mkdir", "/testFolder1/testFolder2");
    toCompare.append(getCommandOutput(new String[] {"mkdir", "/testFolder1/testFolder2"}));
    mFsShell.run("touch", "/testFolder1/testFolder2/testFile2");
    toCompare
        .append(getCommandOutput(new String[] {"touch", "/testFolder1/testFolder2/testFile2"}));
    AlluxioURI testFolder1 = new AlluxioURI("/testFolder1");
    AlluxioURI testFolder2 = new AlluxioURI("/testFolder1/testFolder2");
    AlluxioURI testFile2 = new AlluxioURI("/testFolder1/testFolder2/testFile2");
    Assert.assertTrue(fileExist(testFolder1));
    Assert.assertTrue(fileExist(testFolder2));
    Assert.assertTrue(fileExist(testFile2));
    mFsShell.run("rm", "/testFolder1/testFolder2/testFile2");
    toCompare.append(getCommandOutput(new String[] {"rm", "/testFolder1/testFolder2/testFile2"}));
    Assert.assertEquals(toCompare.toString(), mOutput.toString());
    Assert.assertTrue(fileExist(testFolder1));
    Assert.assertTrue(fileExist(testFolder2));
    Assert.assertFalse(fileExist(testFile2));
  }

  @Test
  public void rmWildCardTest() throws IOException, AlluxioException {
    AlluxioShellUtilsTest.resetFileHierarchy(mFileSystem);

    mFsShell.run("rm", "/testWildCards/foo/foo*");
    Assert.assertFalse(fileExist(new AlluxioURI("/testWildCards/foo/foobar1")));
    Assert.assertFalse(fileExist(new AlluxioURI("/testWildCards/foo/foobar2")));
    Assert.assertTrue(fileExist(new AlluxioURI("/testWildCards/bar/foobar3")));

    mFsShell.run("rm", "/testWildCards/*");
    Assert.assertFalse(fileExist(new AlluxioURI("/testWildCards/foobar4")));
    Assert.assertTrue(fileExist(new AlluxioURI("/testWildCards/foo")));
    Assert.assertTrue(fileExist(new AlluxioURI("/testWildCards/bar")));
    Assert.assertTrue(fileExist(new AlluxioURI("/testWildCards/bar/foobar3")));
  }
}

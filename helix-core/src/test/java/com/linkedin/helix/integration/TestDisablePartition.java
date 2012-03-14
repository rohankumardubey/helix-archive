package com.linkedin.helix.integration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.linkedin.helix.TestHelper;
import com.linkedin.helix.manager.zk.ZKHelixAdmin;
import com.linkedin.helix.tools.ClusterStateVerifier;

public class TestDisablePartition extends ZkStandAloneCMTestBase
{
  private static Logger LOG = Logger.getLogger(TestDisablePartition.class);

  @Test()
  public void testDisablePartition() throws InterruptedException
  {
    LOG.info("START testDisablePartition() at " + new Date(System.currentTimeMillis()));

    // localhost_12919 is MASTER for TestDB_0
    ZKHelixAdmin tool = new ZKHelixAdmin(_zkClient);
    tool.enablePartition(CLUSTER_NAME, "localhost_12919", "TestDB", "TestDB_0", false);
    Map<String, Set<String>> disabledPartMap = new HashMap<String, Set<String>>()
    {
      {
        put("TestDB_0", TestHelper.setOf("localhost_12919"));
      }
    };

    boolean result = ClusterStateVerifier.verify(
        new ClusterStateVerifier.BestPossAndExtViewVerifier(ZK_ADDR, CLUSTER_NAME));
    Assert.assertTrue(result);

    TestHelper.verifyState(CLUSTER_NAME, ZK_ADDR, disabledPartMap, "OFFLINE");

    tool.enablePartition(CLUSTER_NAME, "localhost_12919", "TestDB", "TestDB_0", true);
    
    result = ClusterStateVerifier.verify(
        new ClusterStateVerifier.BestPossAndExtViewVerifier(ZK_ADDR, CLUSTER_NAME));
    Assert.assertTrue(result);

    LOG.info("STOP testDisablePartition() at " + new Date(System.currentTimeMillis()));

  }

}
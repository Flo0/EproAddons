package com.gestankbratwurst.eproaddons.addons.christmas.christmasboss;

import java.util.LinkedList;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 26.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class SnowBossThread implements Runnable {

  private final LinkedList<SnowBossTask> snowBossTasks;

  public SnowBossThread() {
    this.snowBossTasks = new LinkedList<>();
  }

  public void addTask(final SnowBossTask task) {
    this.snowBossTasks.add(task);
  }

  @Override
  public void run() {
    this.snowBossTasks.removeIf(SnowBossTask::tick);
  }

}

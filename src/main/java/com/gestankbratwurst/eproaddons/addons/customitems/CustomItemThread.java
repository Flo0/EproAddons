package com.gestankbratwurst.eproaddons.addons.customitems;

import java.util.Iterator;
import java.util.LinkedList;
import lombok.AccessLevel;
import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 23.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class CustomItemThread implements Runnable {

  @Getter(AccessLevel.PROTECTED)
  private static final CustomItemThread instance = new CustomItemThread();

  private CustomItemThread() {
    this.taskQueue = new LinkedList<>();
  }

  public void addTask(final CustomItemTask customItemTask) {
    this.taskQueue.add(customItemTask);
  }

  private final LinkedList<CustomItemTask> taskQueue;

  @Override
  public void run() {
    final Iterator<CustomItemTask> iterator = this.taskQueue.iterator();
    while (iterator.hasNext()) {
      final CustomItemTask next = iterator.next();
      next.tick();
      if (next.isDone()) {
        iterator.remove();
      }
    }
  }

}

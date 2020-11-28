package com.gestankbratwurst.eproaddons.util.functional;

import com.gestankbratwurst.eproaddons.util.tasks.TaskManager;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of JavaPlugin and was created at the 27.10.2020
 *
 * JavaPlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class FutureOperations {

  public static <T> CompletableFuture<List<T>> waitForAsync(final List<CompletableFuture<T>> fu) {
    return CompletableFuture.supplyAsync(() -> fu.stream().map(CompletableFuture::join).collect(Collectors.toList()),
        TaskManager.getInstance().getScheduler());
  }

  public static <T> CompletableFuture<List<T>> waitNonBlocking(final List<CompletableFuture<T>> fu) {
    return CompletableFuture.allOf(fu.toArray(new CompletableFuture[0])).thenApply(future -> fu.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList()));
  }

}
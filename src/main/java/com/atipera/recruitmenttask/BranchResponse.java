package com.atipera.recruitmenttask;

public record BranchResponse(String name, Commit commit) {
    public record Commit(String sha) {}
}
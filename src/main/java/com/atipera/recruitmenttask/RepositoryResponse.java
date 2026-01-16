package com.atipera.recruitmenttask;

import java.util.List;

public class RepositoryResponse {
    private String name;
    private Owner owner;
    private boolean fork;
    private List<BranchResponse> branches;

    public RepositoryResponse() {
    }

    public RepositoryResponse(String name, Owner owner, boolean fork) {
        this.name = name;
        this.owner = owner;
        this.fork = fork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public List<BranchResponse> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchResponse> branches) {
        this.branches = branches;
    }

    public static class Owner {
        private String login;

        public Owner() {
        }

        public Owner(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }
}
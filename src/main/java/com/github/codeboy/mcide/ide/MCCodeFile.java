package com.github.codeboy.mcide.ide;

import com.github.codeboy.piston4j.api.CodeFile;

public class MCCodeFile {
    private String name;
    private String[] content;
    private boolean mainFile;
    private transient CodeProject project;

    public MCCodeFile(String name, String... content) {
        this.name = name;
        this.content = content;
    }

    public MCCodeFile(String... content) {
        this.content = content;
    }

    public String getName() {
        return name == null ? "unnamed" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String... content) {
        this.content = content;
    }

    public boolean isMainFile() {
        return mainFile;
    }

    public void setMainFile(boolean mainFile) {
        this.mainFile = mainFile;
    }

    public CodeProject getProject() {
        return project;
    }

    public void setProject(CodeProject project) {
        if (this.project != null)
            return;
//            throw new IllegalStateException("Project already set");
        this.project = project;
    }

    public CodeFile toCodeFile() {
        String content = String.join("", this.content);
        return name != null ? new CodeFile(name, content) : new CodeFile(content);
    }
}

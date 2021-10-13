package com.github.codeboy.mcide.ide;

import com.github.codeboy.piston4j.api.CodeFile;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class MCCodeFileTest {

    private Random random;

    @Before
    public void setUp() {
        random = new Random();
    }

    private String randomString(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return new String(bytes);
    }

    @Test
    public void getName() {
        MCCodeFile codeFile = new MCCodeFile();
        assertEquals("Name of codefile without name should be \"unnamed\"", "unnamed", codeFile.getName());
    }

    @Test
    public void setName() {
        MCCodeFile codeFile = new MCCodeFile();
        String name = randomString(10);

        codeFile.setName(name);

        assertEquals("Name of codefile didn´t return correct name after setting it", name, codeFile.getName());
    }

    @Test
    public void getContent() {
        MCCodeFile codeFile = new MCCodeFile();
        String[] content = codeFile.getContent();

        assertArrayEquals("Content should be empty by default", new String[0], content);
    }

    @Test
    public void setContent() {
        MCCodeFile codeFile = new MCCodeFile();
        String[] expectedContent = {randomString(5), randomString(5)};
        codeFile.setContent(expectedContent);

        String[] content = codeFile.getContent();

        assertArrayEquals("Content not the same after changing it", expectedContent,content);
    }

    @Test
    public void isMainFile() {
        MCCodeFile codeFile = new MCCodeFile();
        assertFalse("Codefile should not be main file by default", codeFile.isMainFile());
    }

    @Test
    public void setMainFile() {
        MCCodeFile codeFile = new MCCodeFile();
        codeFile.setMainFile(true);
        assertTrue("Codefile is not main file after setting it as main file", codeFile.isMainFile());
    }

    @Test
    public void getProject() {
        MCCodeFile codeFile = new MCCodeFile();
        assertNull("Project should be null by default", codeFile.getProject());
    }

    @Test
    public void setProject() {

    }

    @Test
    public void toCodeFile() {
        MCCodeFile mcCodeFile = new MCCodeFile();

        assertCodeFileEquals(mcCodeFile, mcCodeFile.toCodeFile());

        mcCodeFile.setContent(randomString(15), randomString(20));

        assertCodeFileEquals(mcCodeFile, mcCodeFile.toCodeFile());
    }

    private void assertCodeFileEquals(MCCodeFile mcCodeFile, CodeFile codeFile) {
        assertNotNull("McCodeFile should not be null", mcCodeFile);
        assertNotNull("CodeFile should not be null", codeFile);

        // no further tests because there currently CodeFile does not offer a way of accessing the content or name
    }
}
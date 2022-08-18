package com.sensibol.android.base

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.FileWriter

@RunWith(JUnit4::class)
public class ExtensionsKtTest {

    @get:Rule
    public val temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun test_getMd5_returns_correct_hash() {
        val file: File = temporaryFolder.newFolder("reports")
            .toPath()
            .resolve("output.txt")
            .toFile()

        val writer = FileWriter(file)
        writer.write("singstr")
        writer.close()

        val actual = file.md5

        val expected = "F5BB42B221C584CD9592E38EFD3D79CD"

        assertEquals(expected.toLowerCase(), actual.toLowerCase())
    }
}
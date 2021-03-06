/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2020 minecraft-dev
 *
 * MIT License
 */

@file:JvmName("TestUtil")
package com.demonwav.mcdev.framework

import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.DebugUtil
import com.intellij.testFramework.LexerTestCase
import com.intellij.util.ReflectionUtil
import org.junit.jupiter.api.Assertions

typealias ProjectBuilderFunc =
    ProjectBuilder.(path: String, code: String, configure: Boolean, allowAst: Boolean) -> VirtualFile

val mockJdk by lazy {
    val path = findLibraryPath("mockJDK")
    val rtFile = StandardFileSystems.local().findFileByPath(path)!!
    val rt = JarFileSystem.getInstance().getRootByLocal(rtFile)!!
    val home = rtFile.parent!!
    MockJdk("1.7", rt, home)
}

fun findLibraryPath(name: String) = FileUtil.toSystemIndependentName(System.getProperty("testLibs.$name")!!)
private fun findLibrary(name: String) = StandardFileSystems.jar()
    .refreshAndFindFileByPath(findLibraryPath(name) + JarFileSystem.JAR_SEPARATOR)

fun createLibrary(project: Project, name: String): Library {
    val table = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
    return table.getLibraryByName(name) ?: run {
        val library = table.createLibrary(name)
        val libraryModel = library.modifiableModel
        libraryModel.addRoot(findLibrary(name)!!, OrderRootType.CLASSES)
        libraryModel.commit()
        library
    }
}

fun testLexer(basePath: String, lexer: Lexer) {
    val caller = ReflectionUtil.getCallerClass(3)!!
    val text = caller.getResource(basePath).readText().trim()

    val expected = caller.getResource("${basePath.substringBeforeLast('.')}.txt").readText().trim()
    val actual = LexerTestCase.printTokens(text.filter { it != '\r' }, 0, lexer)

    val expectedLines = expected.lineSequence().filter { it.isNotBlank() }.toList()
    val actualLines = actual.lineSequence().filter { it.isNotBlank() }.toList()
    Assertions.assertLinesMatch(expectedLines, actualLines)
}

fun ProjectBuilderTest.testParser(basePath: String, func: ProjectBuilderFunc) {
    val caller = ReflectionUtil.getCallerClass(3)!!
    val text = caller.getResource(basePath).readText().trim()
    val expected = caller.getResource("${basePath.substringBeforeLast('.')}.txt").readText().trim()

    var file: PsiFile? = null
    buildProject {
        file = func(basePath.substringAfterLast('/'), text, true, false).toPsiFile()
    }

    val actual = DebugUtil.psiToString(file!!, false, true)

    val expectedLines = expected.lineSequence().filter { it.isNotBlank() }.toList()
    val actualLines = actual.lineSequence().filter { it.isNotBlank() }.toList()
    Assertions.assertLinesMatch(expectedLines, actualLines)
}

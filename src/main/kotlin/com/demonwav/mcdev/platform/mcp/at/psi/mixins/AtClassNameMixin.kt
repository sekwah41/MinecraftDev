/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.at.psi.mixins

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement

interface AtClassNameMixin : PsiElement {

    val classNameElement: PsiElement
    val classNameValue: PsiClass?
    val classNameText: String

    fun setClassName(className: String)
}

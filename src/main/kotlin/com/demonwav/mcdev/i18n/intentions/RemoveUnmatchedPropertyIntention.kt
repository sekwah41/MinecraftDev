/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.i18n.intentions

import com.demonwav.mcdev.i18n.lang.gen.psi.I18nProperty
import com.demonwav.mcdev.i18n.lang.gen.psi.I18nTypes
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException

class RemoveUnmatchedPropertyIntention : PsiElementBaseIntentionAction() {
    override fun getText() = "Remove translation"

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val elem = element as? I18nProperty ?: element.parent
        if (elem.nextSibling?.node?.elementType === I18nTypes.LINE_ENDING) {
            elem.nextSibling.delete()
        }
        elem.delete()
    }

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement) = true

    override fun getFamilyName() = "Minecraft"
}

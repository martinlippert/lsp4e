/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.lsp4e.operations.codelens;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensOptions;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.services.LanguageServer;

public class LSPCodeMining extends LineHeaderCodeMining {

	private CodeLens codeLens;
	private LanguageServer languageServer;
	private CodeLensOptions codeLensOptions;

	public LSPCodeMining(CodeLens codeLens, IDocument document, LanguageServer languageServer,
			CodeLensOptions codeLensOptions, CodeLensProvider provider) throws BadLocationException {
		super(codeLens.getRange().getStart().getLine(), document, provider);
		this.codeLens = codeLens;
		this.languageServer = languageServer;
		this.codeLensOptions = codeLensOptions;
		setLabel(getCodeLensString(codeLens));
	}

	protected static @Nullable String getCodeLensString(@NonNull CodeLens codeLens) {
		Command command = codeLens.getCommand();
		if (command == null || command.getTitle().isEmpty()) {
			return null;
		}
		return command.getTitle();
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		if (this.codeLensOptions.isResolveProvider()) {
			return languageServer.getTextDocumentService().resolveCodeLens(this.codeLens).thenAccept(codeLens -> {
				this.codeLens = codeLens;
				setLabel(getCodeLensString(codeLens));
			});
		}
		return CompletableFuture.completedFuture(null);
	}


}

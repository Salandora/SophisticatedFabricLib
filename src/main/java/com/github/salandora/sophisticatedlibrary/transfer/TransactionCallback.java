package com.github.salandora.sophisticatedlibrary.transfer;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class TransactionCallback {
	public static void onSuccess(TransactionContext context, Runnable onSuccess) {
		context.addCloseCallback(new TransactionCallback.Success(onSuccess));
	}

	private record Success(Runnable onSuccess) implements TransactionContext.CloseCallback {
		@Override
		public void onClose(TransactionContext transaction, TransactionContext.Result result) {
			if (result.wasAborted()) {
				return;
			}

			if (transaction.nestingDepth() > 0) {
				transaction.getOpenTransaction(transaction.nestingDepth() - 1).addCloseCallback(this);
			} else {
				onSuccess.run();
			}
		}
	}
}

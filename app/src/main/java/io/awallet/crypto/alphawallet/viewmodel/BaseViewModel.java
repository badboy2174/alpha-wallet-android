package io.awallet.crypto.alphawallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import io.awallet.crypto.alphawallet.C;
import io.awallet.crypto.alphawallet.entity.BaseViewCallback;
import io.awallet.crypto.alphawallet.entity.ErrorEnvelope;
import io.awallet.crypto.alphawallet.entity.ServiceException;
import io.awallet.crypto.alphawallet.entity.Token;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends ViewModel
{
	protected final MutableLiveData<ErrorEnvelope> error = new MutableLiveData<>();
	protected final MutableLiveData<Boolean> progress = new MutableLiveData<>();
	protected Disposable disposable;
	protected static final MutableLiveData<Integer> queueCompletion = new MutableLiveData<>();
	protected static final MutableLiveData<String> pushToastMutable = new MutableLiveData<>();
	protected static final MutableLiveData<Integer> successDialogMutable = new MutableLiveData<>();
	protected static final MutableLiveData<Integer> errorDialogMutable = new MutableLiveData<>();
	protected static final MutableLiveData<Boolean> refreshTokens = new MutableLiveData<>();

	@Override
	protected void onCleared()
	{
		cancel();
	}

	private void cancel()
	{
		if (disposable != null && !disposable.isDisposed())
		{
			disposable.dispose();
		}
	}

	public LiveData<ErrorEnvelope> error()
	{
		return error;
	}

	public LiveData<Boolean> progress()
	{
		return progress;
	}

	public LiveData<Integer> queueProgress()
	{
		return queueCompletion;
	}

	public LiveData<String> pushToast()
	{
		return pushToastMutable;
	}

	public LiveData<Integer> marketQueueSuccessDialog() {
		return successDialogMutable;
	}

	public LiveData<Integer> marketQueueErrorDialog() {
		return errorDialogMutable;
	}

	public LiveData<Boolean> refreshTokens() {
		return refreshTokens;
	}

	protected void onError(Throwable throwable)
	{
		Log.d("TAG", "Err", throwable);
		if (throwable instanceof ServiceException)
		{
			error.postValue(((ServiceException) throwable).error);
		}
		else
		{
			if (throwable.getCause() == null || TextUtils.isEmpty(throwable.getCause().getMessage()))
			{
				error.postValue(new ErrorEnvelope(C.ErrorCode.UNKNOWN, null, throwable));
			}
			else
			{
				error.postValue(new ErrorEnvelope(C.ErrorCode.UNKNOWN, throwable.getCause().getMessage(), throwable));
			}
		}
	}

	public static void onQueueUpdate(int complete)
	{
		queueCompletion.postValue(complete);
	}

	public static void onPushToast(String message)
	{
		pushToastMutable.postValue(message);
	}

	protected BaseViewCallback processMessages = new BaseViewCallback() {
		@Override
		public void queueUpdate(int complete) {
			onQueueUpdate(complete);
		}

		@Override
		public void pushToast(String message) {
			onPushToast(message);
		}

		@Override
		public void showMarketQueueSuccessDialog(Integer resId) {
			onMarketQueueSuccess(resId);
		}

		@Override
		public void showMarketQueueErrorDialog(Integer resId) {
			onMarketQueueError(resId);
		}
	};

	public static void onMarketQueueError(Integer resId) {
		errorDialogMutable.postValue(resId);
	}

	public static void onMarketQueueSuccess(Integer resId) {
		successDialogMutable.postValue(resId);
	}

	public void showSendToken(Context context, String address, String symbol, int decimals, Token token) {
		//do nothing
	}

	public void showRedeemToken(Context context, Token token) {
		//do nothing
	}
}
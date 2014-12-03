package soft.stwktw.InappbillingGoogle;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

public class Iab {

	private static Iab instance;
	public static final int RC_REQUEST = 10001;
	
	/**
	 * 인스턴스를 반환합니다.
	 * @return
	 */
	public static Iab getInstance(){
		if(instance == null){
			instance = new Iab();
		}
		return instance;
	}
	
	private Iab(){};
	
	private IabHelper mHelper;
	private IabListener listener;
	private Context context;
	private String payload;
	
	/**
	 * 구글 인앱 결제 설정을 합니다. 
	 * @param context
	 * @param aListener
	 * @param payload
	 * @param base64PublicKey
	 */
	public void setup(Context context, IabListener aListener, String payload, String base64PublicKey){
		if(mHelper != null) return;
		
		if(context == null) return;
		if(aListener == null) return;
		
		this.context = context;
		this.listener = aListener;
		this.payload = payload;
		
		mHelper = new IabHelper(context, base64PublicKey);
		// 디버그 모드일 때 로깅
		mHelper.enableDebugLogging(isDebugMode(context));

		// onIabSetupFinished 이벤트 발생
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                	listener.OnErrorMessage("Problem setting up in-app billing: " + result);
                    return;
                }else{
                	listener.OnSetupFinished();
                }
            }
        });
	}
	
	/**
     * 구글 인앱 결제 모듈의 사용을 종료합니다.
     * 앱의 종료 전에 꼭 실행되어야 합니다.
     */
	public void dispose(){
		try
    	{
        	// very important:
            if (mHelper != null) {
                mHelper.dispose();
                mHelper = null;
            }
    	}
    	catch(Exception e)
    	{
    		listener.OnErrorMessage(e.toString());
    	}
	}
	
	/**
     * 구매 목록을 호출 합니다.
     * listener를 통해 구매목록을 확인 할 수 있습니다. 
     */
	public void queryInventoryAsync(){
		mHelper.queryInventoryAsync(mGotInventoryListener);
	}
	
	private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
    	public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
            	listener.OnErrorMessage("Failed to query inventory: " + result);
                return;
            }
            listener.OnIventory(inventory);
        }
    };
    
    /**
     * 구매한 제품을 소비합니다.
     * listener를 통해 소비 결과를 확인 할 수 있습니다.
     * (관리되지 않는 제품은 소비를 하지 않으면 재구매를 할 수 없습니다.)
     * @param purchase
     */
    public void consumeAsync(Purchase purchase){
    	mHelper.consumeAsync(purchase, mConsumeFinishedListener);
    }
    
    private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			// TODO Auto-generated method stub
			if (result.isFailure()) {
            	listener.OnErrorMessage("Error consuming: " + result);
                return;
            }
			listener.OnConsume(purchase);
		}
    };
    
    /**
     * 제품을 구매합니다.
     * listener를 통해 구매 결과를 확인 할 수 있습니다.
     * @param productId
     */
    public void purchaseProduct(String productId){
    	try
    	{
	        mHelper.launchPurchaseFlow((Activity) context, productId, IabHelper.ITEM_TYPE_INAPP, Iab.RC_REQUEST,
	                mPurchaseFinishedListener, payload);
    	}
    	catch(Exception e)
    	{
    		listener.OnErrorMessage(e.toString());
    	}
    }
    
    /**
     * 제품을 구독합니다.
     * listener를 통해 구독 결과를 확인 할 수 있습니다.
     * @param productId
     */
    public void purchaseSubscriptions(String productId){
    	try
    	{
	        mHelper.launchPurchaseFlow((Activity) context, productId, IabHelper.ITEM_TYPE_SUBS, Iab.RC_REQUEST,
	                mPurchaseFinishedListener, payload);
    	}
    	catch(Exception e)
    	{
    		listener.OnErrorMessage(e.toString());
    	}
    }
    
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
            	listener.OnErrorMessage("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
            	listener.OnErrorMessage("Error purchasing. Authenticity verification failed.");
                return;
            }

            listener.OnPurchase(purchase);
        }
    };
    
    private boolean verifyDeveloperPayload(Purchase p) {
    	boolean result = false;
    	
    	String payload = p.getDeveloperPayload();
    	
    	try{
    		if(this.payload.equals(payload) == true){
    			result = true;
    		}
    	}catch(Exception e){
    		
    	}

        return result;
    }
    
    private boolean isDebugMode(Context context){
    	final ApplicationInfo appInfo = context.getApplicationInfo();
    	return (0 != (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    /**
     * IabHelper 를 반환합니다.
     * @return
     */
	public IabHelper getmHelper() {
		return mHelper;
	}
}

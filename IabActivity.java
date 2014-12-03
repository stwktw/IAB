package soft.stwktw.InappbillingGoogle;

import android.app.Activity;
import android.content.Intent;

public abstract class IabActivity extends Activity implements IabListener{
	
    private Iab iab;
    
    /**
     * 구글 인앱 결제 설정을 합니다. 
     * @param payload
     * @param base64PublicKey
     */
    protected void setup(String payload, String base64PublicKey){
    	iab = Iab.getInstance();
        iab.setup(this, this, payload, base64PublicKey);
    }
    
    /**
     * 구글 인앱 결제 모듈의 사용을 종료합니다.
     * 앱의 종료 전에 꼭 실행되어야 합니다.
     */
    protected void dispose(){
    	iab.dispose();
    }
    
    /**
     * 구매 목록을 호출 합니다.
     * OnIventoryActivity 함수를 통해 구매목록을 확인 할 수 있습니다. 
     */
    protected void queryInventoryAsync(){
    	iab.queryInventoryAsync();
    }
    
    /**
     * 구매한 제품을 소비합니다.
     * OnConsumeActivity 함수를 통해 소비 결과를 확인 할 수 있습니다.
     * (관리되지 않는 제품은 소비를 하지 않으면 재구매를 할 수 없습니다.)
     * @param purchase
     */
    protected void consumeAsync(Purchase purchase){
    	iab.consumeAsync(purchase);
    }
    
    /**
     * 제품을 구매합니다.
     * OnPurchaseActivity 함수를 통해 구매 결과를 확인 할 수 있습니다.
     * @param productId
     */
    protected void purchaseProduct(String productId){
    	iab.purchaseProduct(productId);
    }
    
    /**
     * 제품을 구독합니다.
     * OnPurchaseActivity 함수를 통해 구독 결과를 확인 할 수 있습니다.
     * @param productId
     */
    protected void purchaseSubscriptions(String productId){
    	iab.purchaseSubscriptions(productId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (iab.getmHelper() == null) return;

        // Pass on the activity result to the helper for handling
        if (!iab.getmHelper().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
        }
    }
    
	@Override
	public void OnErrorMessage(String message) {
		// TODO Auto-generated method stub
		OnErrorMessageActivity(message);
	}

	@Override
	public void OnIventory(Inventory inventory) {
		// TODO Auto-generated method stub
		OnIventoryActivity(inventory);
	}

	@Override
	public void OnConsume(Purchase purchase) {
		// TODO Auto-generated method stub
		OnConsumeActivity(purchase);
	}

	@Override
	public void OnPurchase(Purchase purchase) {
		// TODO Auto-generated method stub
		OnPurchaseActivity(purchase);
	}
	
	@Override
	public void OnSetupFinished() {
		// TODO Auto-generated method stub
		OnSetupFinishedActivity();
	}
    
	/**
	 * 구글 결제 모듈에서 발생 하는 오류 메세지를 전달합니다.
	 * @param message
	 */
    protected abstract void OnErrorMessageActivity(String message);
    
    /**
     * 구글 결제 로 구매한 목록이 있으면 전달합니다.
     * 구매한 구독 또는 관리되는 제품 목록을 전달합니다.
     * 관리되지 않는 제품인 경우, 소비를 하지 않은 경우 목록을 전달합니다. 
     * 소비를 한 경우, 구매 목록에 나타나지 않습니다.
     * queryInventoryAsync 호출 했을 때, 결과를 전달하는 함수 입니다. 
     * @param inventory
     */
    protected abstract void OnIventoryActivity(Inventory inventory);
    
    /**
     * 제품 소비가 성공한 경우 제품 정보를 전달합니다.
     * @param purchase
     */
    protected abstract void OnConsumeActivity(Purchase purchase);
    
    /**
     * 제품 구매가 성공한 경우 제품 정보를 전달합니다.
     * @param purchase
     */
    protected abstract void OnPurchaseActivity(Purchase purchase);
    
    protected abstract void OnSetupFinishedActivity();

}


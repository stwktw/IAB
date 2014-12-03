package soft.stwktw.InappbillingGoogle;

public interface IabListener {
	public void OnErrorMessage(String message);
	public void OnIventory(Inventory inventory);
	public void OnConsume(Purchase purchase);
	public void OnPurchase(Purchase purchase);
	public void OnSetupFinished();
}

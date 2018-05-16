package mdpm.di1.direct

trait Order {
  def amount: Double
}
trait PizzaOrder extends Order

trait CreditCard

trait CreditCardProcessor {
  def charge(creditCard: CreditCard, amount: Double): ChargeResult
}
class PaypalCreditCardProcessor extends CreditCardProcessor {
  override def charge(creditCard: CreditCard, amount: Double): ChargeResult = {
    new ChargeResult {
      override def successful: Boolean = false
    }
  }
}

trait Receipt
object Receipt {
  def forSuccessfulCharge(amount: Double): Receipt =
    new Receipt {}
  def forDeclinedCharge(msg: String): Receipt =
    new Receipt {}
}

trait ChargeResult {
  def successful: Boolean
}

trait TransactionLog {
  def logChargeResult(result: ChargeResult)
}
class DatabaseTransactionLog extends TransactionLog {
  override def logChargeResult(result: ChargeResult): Unit = {

  }
}

trait BillingService {
  def chargeOrder(order: PizzaOrder, creditCard: CreditCard): Receipt
}

class RealBillingService extends BillingService {
  override def chargeOrder(order: PizzaOrder, creditCard: CreditCard): Receipt = {
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // The 'RealBillingService' should not be responsible for looking
    // up the 'CreditCardProcessor' and 'TransactionLog'
    val processor: CreditCardProcessor = new PaypalCreditCardProcessor
    val transactionLog: TransactionLog = new DatabaseTransactionLog
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    val result = processor.charge(creditCard, order.amount)
    transactionLog.logChargeResult(result)

    if (result.successful) {
      Receipt.forSuccessfulCharge(order.amount)
    } else Receipt.forDeclinedCharge("")
  }
}

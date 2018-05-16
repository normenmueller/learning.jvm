package mdpm.di2.const

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

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// The dependency is exposed in the API signature.
//
// Instead of creating dependent service implementations inside the service
// itself (cf. [mdpm.di1.direct.RealBillingService]), references to dependent
// services are "injected".
//
class RealBillingService(
  val processor     : CreditCardProcessor
, val transactionLog: TransactionLog
) extends BillingService {
// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
  override def chargeOrder(order: PizzaOrder, creditCard: CreditCard): Receipt = {
    val result = processor.charge(creditCard, order.amount)
    transactionLog.logChargeResult(result)

    if (result.successful) {
      Receipt.forSuccessfulCharge(order.amount)
    } else Receipt.forDeclinedCharge("")
  }
}

object Main extends App {
  val processor = new PaypalCreditCardProcessor
  val transactionLog = new DatabaseTransactionLog
  val billingService = new RealBillingService(processor, transactionLog)
}

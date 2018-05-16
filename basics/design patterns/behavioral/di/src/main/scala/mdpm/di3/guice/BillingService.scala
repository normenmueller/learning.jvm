package mdpm.di3.guice

import com.google.inject.{AbstractModule, Guice}
import javax.inject.Inject

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

class RealBillingService @Inject() (
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

/* Guice needs to be configured to build the object graph exactly as you want
 * it. Guice uses bindings to map types to their implementations.
 *
 * A module is a collection of bindings specified using fluent, English-like
 * method calls. The modules are the building blocks of an injector, which is
 * Guice's object-graph builder.
 */
class BillingModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CreditCardProcessor]).to(classOf[PaypalCreditCardProcessor])
    bind(classOf[TransactionLog]).to(classOf[DatabaseTransactionLog])
    bind(classOf[BillingService]).to(classOf[RealBillingService])
  }
}

object Main extends App {
  // Instruct Guice to build the object graph according to the given module
  val injector = Guice.createInjector(new BillingModule)

  // Instruct Guice and the object graph builder 'injector', in particular
  // to instantiate the object graph
  val billingService = injector.getInstance(classOf[BillingService])
}

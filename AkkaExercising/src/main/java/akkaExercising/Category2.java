package akkaExercising;
import akka.actor.AbstractActorWithStash;
import akka.actor.Props;
import akka.actor.UnrestrictedStash;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;


public class Category2 extends AbstractActorWithStash{
	  
	
	private final PartialFunction<Object, BoxedUnit> stashing;
	private final PartialFunction<Object, BoxedUnit> proccessing;
	private int count = 0 ;

	
	static Props props() {
		return Props.create(Category2.class);
	}
	
	{
		stashing = ReceiveBuilder
				.match(ParentRouter.Message.class ,  msg ->{ loggerStash(msg , this);})
				.matchEquals("ping", msg -> { 
					sender().tell("pong", self());
					})
				.build();
		
		proccessing = ReceiveBuilder
				.match(ParentRouter.Message.class ,  msg ->{ loggerProccess(msg , this);})
				.matchEquals("ping", msg -> { 
					sender().tell("pong", self());
					})
				.build();
		receive(stashing);
	}
	
	

	private void loggerStash(ParentRouter.Message message , UnrestrictedStash stash) {
			count++;
			stash.stash();
			System.out.println("Stashing messages Category 2, Message number " + count);
			
	if (count==5) {	
			System.out.println("Switched to proccessing");
			stash.unstashAll();
			getContext().become(proccessing);
		
		}
	
	}
	
	private void loggerProccess(ParentRouter.Message message , UnrestrictedStash stash ) {
		System.out.println("Message proccesed by Car Category 2 " +  message.messageBody);
			count--;
			if (count==0) {
				getContext().become(stashing);
				System.out.println("switching back to Sttashing mode");
			}
			System.out.println(count);

		
	}
	
}
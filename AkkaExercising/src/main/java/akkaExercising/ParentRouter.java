package akkaExercising;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
public class ParentRouter  extends AbstractLoggingActor{
	
	static class Message {
		public final String categoryHeader;
		public final String messageBody;
		public Message(String categoryHeader , String messageBody) {
			this.categoryHeader = categoryHeader ; 
			this.messageBody = messageBody;
		}
	}
	Timer t  = null; 
	static int pingCounter1 = 0;
	static int pingCounter2 = 0;
	private int model1Counter = 0;
	private int model2Counter = 0;
	
	 ActorRef model1 = getContext().actorOf(Category1.props(), "category1");
	 ActorRef model2 = getContext().actorOf(Category2.props(), "category2");
	
	{
		
		  receive(ReceiveBuilder
	                .match(Message.class, msg -> {forward(msg);})
	                .matchEquals("pingStart", msg -> {System.out.println("Recived " + msg);ping("start");})
	                .matchEquals("pingStop", msg -> { System.out.println("Recived " + msg);ping("stop");})
	                .matchEquals("killTest", msg -> { System.out.println("Recived " + msg);killing();})
	                .matchEquals("reCreate", msg -> { System.out.println("Recived " + msg);reCreate();})
	                .matchAny( msg -> {wrongFormat(msg);})
	                .build()
				  );
		
	}
	
	
	public void ping(String command) {
		TimerTask pingTask = null;  
			
	if (command.equals("start")) { 
			t = new Timer();
			pingTask  = new TimerTask() {

			@Override
			public void run() {
				pingAction();
			}
			};

		t.schedule(pingTask , 0, 5000);
		}
	else if(command.equals("stop")) {
		System.out.println("Ping stopped");
		t.cancel();
		pingCounter1 = 0;
		pingCounter2 = 0;
	}	
	}

	
	
	
	public void pingAction()  {
			Future<Object> future1 = Patterns.ask(model1, "ping", 10000);
			try {
				Await.result(future1, Duration.create(5, TimeUnit.SECONDS));
				System.out.println(++pingCounter1 + ":Ping Pong succeed from Child1");
			} catch (Exception e) {
				System.out.println("Child 1 reply timeout, PING failed");
				killingModel("model1");
			}
			
			Future<Object> future2 = Patterns.ask(model2, "ping", 10000);
			try {
				Await.result(future2, Duration.create(5, TimeUnit.SECONDS));
				System.out.println(++pingCounter2 + ":Ping Pong succeed from Child2");
			} catch (Exception e) {
				System.out.println("Child 2 reply timeout, PING failed");
				killingModel("modle2");
			}
		}
	
	public void killingModel(String victim) {
		switch (victim) {
		case "model1":
			model1.tell(PoisonPill.getInstance(), ActorRef.noSender());
			log().info("model1 instance killed with poison Pill");
			ActorRef newActor = getContext().actorOf(Category1.props(), "category1");
			model1 = newActor;
			log().info("New instance created");
			break;
			
		case "model2":
			model2.tell(PoisonPill.getInstance(), ActorRef.noSender());
			log().info("model1 instance killed with poison Pill");
			ActorRef Actor = getContext().actorOf(Category1.props(), "category1");
			model1 = Actor;
			log().info("New instance created");
			break;
			
		}
	}
	
	// Testing kill 
	public void killing() {
		model1.tell(PoisonPill.getInstance(), ActorRef.noSender());

	}
	// Testing creating 
	public void reCreate() {
		ActorRef newActor = getContext().actorOf(Category1.props(), "category1");
		model1 = newActor;
		System.out.println("model1 created");
	}
	
	private void forward(Message msg) {
		if (msg.categoryHeader.equals("category1")) {
			model1.forward(msg, getContext());
			model1Counter ++ ; 
			log().info(model1Counter +  " Messages Forwarded to Model 1");
		}
		else if (msg.categoryHeader.equals("category2")) {
			model2.forward(msg, getContext());
			model2Counter ++ ; 
			log().info(model2Counter + " Messages Forwarded to Model 2 ");
		}
		
	}
	
	private void wrongFormat(Object msg) {
		log().info("Wron message format");
	}
	
	public static Props props() {
		return Props.create(ParentRouter.class);
	}
	
	public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(
                10,
                Duration.create(10, TimeUnit.SECONDS),
                DeciderBuilder
                        .match(RuntimeException.class, ex -> SupervisorStrategy.restart())
                        .build()
        );
}
	
}

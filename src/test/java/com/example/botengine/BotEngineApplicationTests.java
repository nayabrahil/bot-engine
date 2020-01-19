package com.example.botengine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SpringBootTest
class BotEngineApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
	void fluxZip(){
    	Flux<Integer> evenNumbers = Flux.fromIterable(Arrays.asList(2, 4));
    	Flux<Integer> oddNumbers = Flux.fromIterable(Arrays.asList(1,3,5));

    	Flux<Integer> result = evenNumbers.zipWith(oddNumbers, (a,b) -> a+b);

    	StepVerifier.create(result)
				.expectNext(3)
				.expectNext(7)
				.expectComplete()
				.verify();
	}

    @Test
	void shouldEmployBackPressureForThree(){
    	AtomicInteger causeOfOverFlow = new AtomicInteger();

		StepVerifier.create(Flux.range(1, 100)
				.hide()
				.log()
				.onBackpressureBuffer(3, causeOfOverFlow::set), 0)
				.expectSubscription()
				.thenRequest(1)
				.expectNext(1)
				.thenAwait()
				.thenCancel()
				.verify();

		assert(causeOfOverFlow.get()==5);
	}

	@Test
	void shouldCreateFluxWithOneMono(){


		Mono<Integer> singleMono = Mono.just(1);
		Mono<Integer> mono2 = Mono.just(2);
		Flux<Integer> fluxMap = singleMono.concatWith(mono2).map(n -> n*100);

		StepVerifier.create(fluxMap)
				.expectNext(100)
				.expectNext(200)
				.expectComplete()
				.verify();

	}

    @Test
	void shouldCreateFluxWithOneElement(){

    	Flux<Integer> flux = Flux.fromIterable(Arrays.asList(1));

    	Flux<Integer> fluxMap = flux.map(n -> n*100);

    	Mono<Integer> singleMono = fluxMap.single();

    	StepVerifier.create(fluxMap)
				.expectNext(100)
				.expectComplete()
				.verify();

    	assert(singleMono.block()==100);

	}

    @Test
	void executeFlatMapConcurrently(){
    	Flux<Integer> range = Flux.range(0, 1000);

    	range.buffer(10)
				.log()
				.flatMap(x ->
						Flux.fromIterable(x)
						.map(this::toThreeValues)
						.subscribeOn(Schedulers.parallel())
						.log()
				).blockLast();
	}

	private <V> V toThreeValues(Integer integer) {
    	return (V) Arrays.asList(integer+1, integer+2, integer+3);
	}

	@Test
	void shouldCombineTwoFluxUsingMerge(){
		int min=0, max=6;

		Flux<Integer> evenNumbers = Flux.range(min, max).filter(x -> x%2==0);
		Flux<Integer> oddNumbers = Flux.range(min, max).filter( x -> x%2!=0);

		Flux<Integer> fluxOfNumTillHundred = Flux.merge(
				evenNumbers.delayElements(Duration.ofMillis(500L)),
				oddNumbers.delayElements(Duration.ofMillis(300L)));

		//List<Integer> listOfNumTillHundred = fluxOfNumTillHundred.collectList().block();

    	/*assert (listOfNumTillHundred.size()==100);

		assert (listOfNumTillHundred.get(0)==0);
		assert (listOfNumTillHundred.get(1)==2);
		assert (listOfNumTillHundred.get(2)==4);*/

		StepVerifier.create(fluxOfNumTillHundred)
				.expectNext(1)
				.expectNext(0)
				.expectNext(3)
				.expectNext(5)
				.expectNext(2)
				.expectNext(4)
				.expectComplete()
				.verify();
	}


	@Test
	void shouldCombineTwoFluxUsingConcat(){
    	int min=0, max=6;

    	Flux<Integer> evenNumbers = Flux.range(min, max).filter(x -> x%2==0);
    	Flux<Integer> oddNumbers = Flux.range(min, max).filter( x -> x%2!=0);

    	Flux<Integer> fluxOfNumTillHundred = Flux.concat(evenNumbers, oddNumbers);

    	//List<Integer> listOfNumTillHundred = fluxOfNumTillHundred.collectList().block();

    	/*assert (listOfNumTillHundred.size()==100);

		assert (listOfNumTillHundred.get(0)==0);
		assert (listOfNumTillHundred.get(1)==2);
		assert (listOfNumTillHundred.get(2)==4);*/

		StepVerifier.create(fluxOfNumTillHundred)
				.expectNext(0)
				.expectNext(2)
				.expectNext(4)
				.expectNext(1)
				.expectNext(3)
				.expectNext(5)
				.expectComplete()
				.verify();
	}

    @Test
    void withoutReactorApiReactiveCodeYieldsCallbackHell() {
        /*CompletableFuture.supplyAsync(() -> "custom_action")
                .whenComplete((s, throwable) -> {
                    if (throwable != null) {
                        fallbackAction();
                    } else {
                        CompletableFuture completableFuture = executeAsyncCalls(s);
                        completableFuture.whenComplete((o1, o2) -> {
                        });
                    }

                });*/

        List<String> response = Flux.range(0, 100)
				.map(i -> action())
				.doOnError(fallbackAction())
				.map(this::executeAsyncCalls)
				.collectList()
				.block();

		System.out.println(response.size());
    }

	private Consumer<? super Throwable> fallbackAction() {
		System.out.println("Error occurred while processing aysnc method");
		return (Consumer<Throwable>)  throwable -> System.out.println(throwable);
	}

	private Supplier<String> action(){
    	return () -> "some action";
	}

	private String executeAsyncCalls(Supplier<String> s){
		return "another async call for "+s;
	}
}

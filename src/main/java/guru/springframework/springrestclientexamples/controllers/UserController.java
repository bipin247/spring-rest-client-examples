package guru.springframework.springrestclientexamples.controllers;

import guru.springframework.api.domain.InputForm;
import guru.springframework.springrestclientexamples.services.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 9/22/17.
 */
@Slf4j
@Controller
public class UserController {

    private ApiService apiService;

    public UserController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping({"", "/", "/index"})
    public Mono<String> index() {
        return Mono.just("index");
    }

    @PostMapping("/users")
    public Mono<String> formPost(Model model, ServerWebExchange serverWebExchange) {

        //MultiValueMap<String, String> map = serverWebExchange.getFormData().block();

        // Integer limit = new Integer(map.get("limit").get(0));

        //log.debug("Received Limit value: " + limit);
        //default if null or zero
//        if(limit == null || limit == 0){
//            log.debug("Setting limit to default of 10");
//            limit = 10;
//        }

        // model.addAttribute("users", apiService.getUsers(limit));

       Integer limit =  formDataFromInput(serverWebExchange.getFormData()).getLimit();
        model.addAttribute("users",
                apiService.getUsersForReactive(limit));

        return Mono.just("userlist");
    }

    public Mono<ServerResponse> sampleForm(ServerRequest request) {

        return ServerResponse.ok().render("test");
    }

    public Mono<ServerResponse> displayFormData(ServerRequest request) {

        Mono<MultiValueMap<String, String>> formData = request.formData();

        // BodyExtractor based. It didn't result any value for our program
        // It looks any earlier piece of code (Filter ?) already accessed the body
        // making it empty.

        // Mono<MultiValueMap<String, String>> formData = request.body(BodyExtractors.toFormData());

        Integer limit = formDataFromInput(formData).getLimit();
        return ServerResponse.ok().render("userlist",
                apiService.getUsersForReactive(limit));
    }

    public InputForm formDataFromInput(Mono<MultiValueMap<String, String>> formData){
        InputForm formInput  = new InputForm();

        formData.subscribe(formDataMap ->
                {formInput.setLimit(Integer.valueOf(formDataMap.getFirst("limit")));
                });

        return formInput;
    }
}


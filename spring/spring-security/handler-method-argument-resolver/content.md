```java
/**
 * Spring MVC는 요청이 들어올 때마다 해당 요청을 처리할 컨트롤러 메소드의 매개변수를 결정하기 위해
 * HandlerMethodArgumentResolver 구현체 리스트를 순회한다.
 * 내장된 Resolver, 커스텀 Resolver가 모두 포함되어 있으며, 순서도 중요하다.
 *
 * 또한 HandlerMethodArgumentResolver 구현체가 호출되려면 ArgumentResolvers에 등록되어야 하기 때문에
 * WebConfig와 같은 WebMvcConfigurer를 확장한 설정 클래스를 구현하고 addArgumentResolvers 메소드를 Override하여
 * 이 구현체를 추가해야 한다.
 */
public class AuthenticatedPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 모든 매개변수를 순회 -> 모든 Resolver 순회하여 supportsParameter 메소드가 true를 반환하는 것을 찾는다.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginUser.class) &&
                parameter.hasParameterAnnotation(AuthenticatedUser.class);
    }

    /**
     * supportsParameter 메소드를 통해 선택된 Resolver(true를 반환한 Resolver)의 resolverArgument 메소드를 호출하여
     * 해당 매개변수를 변환한다.
     *
     * 이후에 변환된 매개변수를 컨트롤러 메소드의 인자로 전달한다.
     *
     * 여기서는 굳이 Http Header(Authorization)을 다시 분석하지 않고 FilterChainProxy 호출 시 Spring Security에 등록된 Principal을 반환한다.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal();
    }
}
```

아래는 위의 `HandlerMethodArgumentResolver` 구현체를 `ArgumentResolvers`에 등록하기 위해 만든 Configuration class이다.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(getAuthenticatedPrincipalArgumentResolver());
    }

    private static AuthenticatedPrincipalArgumentResolver getAuthenticatedPrincipalArgumentResolver() {
        return new AuthenticatedPrincipalArgumentResolver();
    }
}
```

이제 이렇게 추가된 `ArgumentResolver`를 통해 우리는 아래와 같이 컨트롤러 메소드에서 인자로 AuthenticatedUser에 대한 정보를 활용할 수 있다.

```java
@RequiredArgsConstructor
@Slf4j
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/profile")
    public MemberProfileResponse profile(@AuthenticatedUser LoginUser user) {
        Member member = memberService.findMemberByEmail(user.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        return MemberProfileResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .build();
    }
}
```
package an.xacml.engine.evaluator;

import java.lang.reflect.Method;

import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.engine.ctx.EvaluationContext;

public class PolicyMatcher implements Matcher {

    // This could be PolicyType or PolicySetType.
    private Object policy;

    protected static final String GET_TARGET = "getTarget";

    public PolicyMatcher(Object policy) {
        this.policy = policy;
    }

    @Override
    public boolean match(EvaluationContext ctx) throws IndeterminateException {
        Object previousPolicySet = ctx.setCurrentEvaluatingPolicy(policy);

        try {
            Class<?> claz = policy.getClass();
            Method method = claz.getMethod(GET_TARGET);
            Object target = method.invoke(policy);

            // return the match result of the target element.
            return EvaluatorFactory.getInstance().getMatcher(target).match(ctx);
        }
        catch (IndeterminateException iEx) {
            throw iEx;
        }
        catch (Throwable t) {
            throw new IndeterminateException("The match operation failed due to error: ", t,
                    Constants.STATUS_SYNTAXERROR);
        }
        finally {
            ctx.setCurrentEvaluatingPolicy(previousPolicySet);
        }
    }
}
package filter;

import model.Plan;

public class ScorePlan {
    private final Plan plan;
    private final int score;

    public ScorePlan(Plan plan, int score) {
        this.plan = plan;
        this.score = score;
    }

    public Plan getPlan() {
        return plan;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return String.format("Plan: %s | Score: %d", 
            plan != null ? plan.getPlanName() : "null", score);
    }
}
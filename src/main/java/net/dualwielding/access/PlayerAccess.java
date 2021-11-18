package net.dualwielding.access;

public interface PlayerAccess {

    void resetLastOffhandAttackTicks();

    void setOffhandAttack();

    boolean isOffhandAttack();

    float getAttackCooldownProgressOffhand(float baseTime);
}

package net.dualwielding.access;

public interface PlayerAccess {

    public void resetLastOffhandAttackTicks();

    public void setOffhandAttack();

    public boolean isOffhandAttack();

    public float getAttackCooldownProgressOffhand(float baseTime);
}

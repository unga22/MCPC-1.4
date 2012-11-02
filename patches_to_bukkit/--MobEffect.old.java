package net.minecraft.server;

import java.io.PrintStream;

public class MobEffect
{
  private int effectId;
  private int duration;
  private int amplification;

  public MobEffect(int paramInt1, int paramInt2, int paramInt3)
  {
    this.effectId = paramInt1;
    this.duration = paramInt2;
    this.amplification = paramInt3;
  }

  public MobEffect(MobEffect paramMobEffect) {
    this.effectId = paramMobEffect.effectId;
    this.duration = paramMobEffect.duration;
    this.amplification = paramMobEffect.amplification;
  }

  public void a(MobEffect paramMobEffect) {
    if (this.effectId != paramMobEffect.effectId) {
      System.err.println("This method should only be called for matching effects!");
    }
    if (paramMobEffect.amplification > this.amplification) {
      this.amplification = paramMobEffect.amplification;
      this.duration = paramMobEffect.duration;
    } else if ((paramMobEffect.amplification == this.amplification) && (this.duration < paramMobEffect.duration)) {
      this.duration = paramMobEffect.duration;
    }
  }

  public int getEffectId() {
    return this.effectId;
  }

  public int getDuration() {
    return this.duration;
  }

  public int getAmplifier() {
    return this.amplification;
  }

  public boolean tick(EntityLiving paramEntityLiving)
  {
    if (this.duration > 0) {
      if (MobEffectList.byId[this.effectId].b(this.duration, this.amplification)) {
        b(paramEntityLiving);
      }
      e();
    }
    return this.duration > 0;
  }

  private int e() {
    return --this.duration;
  }

  public void b(EntityLiving paramEntityLiving) {
    if (this.duration > 0)
      MobEffectList.byId[this.effectId].tick(paramEntityLiving, this.amplification);
  }

  public String d()
  {
    return MobEffectList.byId[this.effectId].c();
  }

  public int hashCode()
  {
    return this.effectId;
  }

  public String toString()
  {
    String str = "";
    if (getAmplifier() > 0)
      str = d() + " x " + (getAmplifier() + 1) + ", Duration: " + getDuration();
    else {
      str = d() + ", Duration: " + getDuration();
    }
    if (MobEffectList.byId[this.effectId].f()) {
      return "(" + str + ")";
    }
    return str;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof MobEffect)) {
      return false;
    }
    MobEffect localMobEffect = (MobEffect)paramObject;
    return (this.effectId == localMobEffect.effectId) && (this.amplification == localMobEffect.amplification) && (this.duration == localMobEffect.duration);
  }
}


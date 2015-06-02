/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.EquineEgg;

import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Horse;

/**
 *
 * @author c45y
 */
public class SpeedUtil {
    private EntityInsentient horse;
    
    public SpeedUtil(Horse horse) {
        CraftLivingEntity e = (CraftLivingEntity) horse;
        this.horse = (EntityInsentient) e.getHandle();
    }

    public double getSpeed() {
        AttributeInstance attributes = this.horse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        return attributes.getValue();
    }

    public void setSpeed(Double speed) {
        AttributeInstance attributes = this.horse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        attributes.setValue(speed);
    }
}

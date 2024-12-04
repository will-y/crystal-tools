package dev.willyelton.crystal_tools.common.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

public class CrystalEnergyStorage extends EnergyStorage {
    public CrystalEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public boolean canAdd(int toAdd) {
        return capacity - energy >= toAdd;
    }

    /**
     * Outside of capability interface, don't want other people adding to generator but need to generate it ourselves
     */
    public void addEnergy(int toAdd) {
        energy += toAdd;
        if (energy > capacity) {
            energy = capacity;
        }
    }

    public void removeEnergy(int toRemove) {
        energy -= toRemove;
        if (energy < 0) {
            energy = 0;
        }
    }

    // TODO: Setters for upgrades later
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
    }

    public int getMaxExtract() {
        return maxExtract;
    }
}

package com.github.salandora.sophisticatedlibrary.event.api.v0.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public interface MobSpawnEvents {
    Event<After> AFTER_FINALIZE_SPAWN = EventFactory.createArrayBacked(After.class, callbacks -> (finalizeEvent) -> {
        for (After event : callbacks) {
            event.onAfterFinalizeSpawn(finalizeEvent);
        }
    });

    @FunctionalInterface
    interface After {
        void onAfterFinalizeSpawn(FinalizeSpawn event);
    }

    class FinalizeSpawn {
        private final Entity entity;
        private final ServerLevelAccessor level;
        private final DifficultyInstance difficulty;
        private final MobSpawnType spawnType;
        private final SpawnGroupData spawnGroupData;
		private final CompoundTag dataTag;

		public FinalizeSpawn(Entity entity, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, CompoundTag dataTag) {
            this.entity = entity;
            this.level = level;
            this.difficulty = difficulty;
            this.spawnType = spawnType;
            this.spawnGroupData = spawnGroupData;
			this.dataTag = dataTag;
		}

        public Entity getEntity() {
            return entity;
        }

        public ServerLevelAccessor getLevel() {
            return level;
        }

        public DifficultyInstance getDifficulty() {
            return difficulty;
        }

        public MobSpawnType getMobSpawnType() {
            return spawnType;
        }

        @Nullable
        public SpawnGroupData getSpawnGroupData() {
            return spawnGroupData;
        }

		public CompoundTag getDataTag() {
			return dataTag;
		}
    }
}

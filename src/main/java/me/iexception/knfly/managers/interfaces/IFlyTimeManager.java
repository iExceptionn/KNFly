package me.iexception.knfly.managers.interfaces;

import java.util.UUID;

public interface IFlyTimeManager {

    void startFlyTimer(UUID uuid, Integer time);
    void stopTimer(UUID uuid, boolean changeTime);
    boolean hasTimer(UUID uuid);
    Integer getFlyTimer(UUID uuid);
}

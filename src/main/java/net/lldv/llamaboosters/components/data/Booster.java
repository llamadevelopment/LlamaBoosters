package net.lldv.llamaboosters.components.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Booster {

    private final String starter;
    private final Type type;
    private final long time;

    public enum Type {
        FLY,
        BREAK,
        XP,
        FALL_DAMAGE
    }

}

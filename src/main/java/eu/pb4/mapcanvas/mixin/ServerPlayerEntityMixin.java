package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements PlayerInterface {
    private final Int2ObjectMap<VirtualDisplay> mapcanvas_displays = new Int2ObjectOpenHashMap<>();
    private final Map<VirtualDisplay, Box> mapcanvas_boxes = new Object2ObjectOpenHashMap<>();

    @Override
    public void mapcanvas_addDisplay(IntList ids, VirtualDisplay display, @Nullable Box box) {
        for (int id : ids) {
            this.mapcanvas_displays.put(id, display);
        }

        if (box != null) {
            this.mapcanvas_boxes.put(display, box);
        }
    }

    @Override
    public void mapcanvas_removeDisplay(IntList ids, VirtualDisplay display) {
        for (int id : ids) {
            this.mapcanvas_displays.remove(id);
        }
        this.mapcanvas_boxes.remove(display);
    }

    @Override
    public VirtualDisplay mapcanvas_getDisplay(int id) {
        return this.mapcanvas_displays.get(id);
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void mapcanvas_disconnect(CallbackInfo ci) {
        this.mapcanvas_removeAll();
    }

    @Override
    public Set<Map.Entry<VirtualDisplay, Box>> mapcanvas_getBoxes() {
        return this.mapcanvas_boxes.entrySet();
    }

    @Unique
    private void mapcanvas_removeAll() {
        for (var entry : new ArrayList<>(this.mapcanvas_displays.values())) {
            entry.removePlayer((ServerPlayerEntity) (Object) this);
        }
        this.mapcanvas_boxes.clear();
    }
}

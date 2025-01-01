package org.hhoa.mc.intensify.core;


public class DefaultEnhancementIntensifySystemTest {
    public static void main(String[] args) {
        DefaultEnhancementIntensifySystem system =
            new DefaultEnhancementIntensifySystem(0.8,
                0.03,
                0.9,
                0.6,
                0.1,
                0.6,
                0.5);
        int level = 0;
        int failuresCount = 0;

        for (int i = 1; i <= 64; i++) {
            DefaultEnhancementIntensifySystem.EnhanceResult enhance = system.enhance(level, failuresCount);
            if (enhance == DefaultEnhancementIntensifySystem.EnhanceResult.UPGRADE) {
                failuresCount = 0;
                level++;
            } else if (enhance == DefaultEnhancementIntensifySystem.EnhanceResult.DOWNGRADE) {
                failuresCount++;
                level--;
            } else {
                failuresCount++;
            }
            System.out.printf("第 %d 次强化：%s，等级：%d，失败次数：%d\n",
                i,
                enhance == DefaultEnhancementIntensifySystem.EnhanceResult.NOT_CHANGE ? "不变" : (enhance == DefaultEnhancementIntensifySystem.EnhanceResult.UPGRADE ? "成功" : "降级"),
                level,
                failuresCount);
        }
    }
}

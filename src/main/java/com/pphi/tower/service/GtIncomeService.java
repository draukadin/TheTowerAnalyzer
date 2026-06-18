package com.pphi.tower.service;

import com.pphi.tower.web.dto.GtIncomeProjectionDto;
import com.pphi.tower.web.dto.GtIncomeProjectionDto.DurationMilestone;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
public class GtIncomeService {

    private static final double[] DURATION_MILESTONES = {15, 20, 25, 30, 35, 40, 45, 50, 53};

    public GtIncomeProjectionDto project(int gtPlusLevel, double gtDurationSec, double gtCooldownSec,
                                          double kps, double totalRunDurationSec, double incomePerMob) {
        double r  = 0.0003 * (gtPlusLevel + 1);
        double K  = kps * gtDurationSec;
        double T  = totalRunDurationSec / gtCooldownSec;
        double FI = T * kps * gtDurationSec * incomePerMob * Math.pow(1 + r, K);

        double FI_perm   = totalRunDurationSec * kps * incomePerMob * Math.pow(1 + r, K);
        double B         = Math.pow(1 + r, K) - 1;
        double K1        = kps * (gtDurationSec + 1);
        double marginal  = T * kps * (gtDurationSec + 1) * incomePerMob * Math.pow(1 + r, K1) - FI;

        TreeSet<Double> durations = new TreeSet<>();
        for (double m : DURATION_MILESTONES) durations.add(m);
        durations.add(gtDurationSec);

        List<DurationMilestone> table = new ArrayList<>();
        for (double d : durations) {
            double Kd  = kps * d;
            double FId = T * kps * d * incomePerMob * Math.pow(1 + r, Kd);
            table.add(new DurationMilestone(d, FId, FId - FI, d == gtDurationSec));
        }

        return new GtIncomeProjectionDto(FI, FI_perm, T, K, B, marginal, table);
    }
}

package com.rationalteam.rtreadymix.routes;

import com.rationalteam.rterp.erpcore.*;
import com.rationalteam.rterp.erpcore.data.TblOption;
import com.rationalteam.rtreadymix.SystemConfig;
import com.rationalteam.rtreadymix.data.Tblorder;
import io.quarkus.qute.TemplateExtension;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TemplateExtension(namespace = "rtutil")
public class RtutilTemplateExtension {
    @Transactional
    public static List<TblOption> list(String tblname) {
        List<TblOption> list = MezoDB.open("select * from " + tblname, TblOption.class);
        return list;
    }

    public static List<String> listTemplate(String type) {
        Stream<Path> list = null;
        try {
            list = Files.list(Paths.get(SystemConfig.TEMPLATE, type));
            return list.map(f -> f.getFileName().toString()).collect(Collectors.toList());
        } catch (IOException e) {
            Utility.ShowError(e);
            return new ArrayList<>();
        }
    }

    @Transactional
    public static List<Tblorder> listOrders(Integer clid) {
        List<Tblorder> orders = new ArrayList<>();
        try {
            orders = MezoDB.open("select * from tblorder where clientid=" + clid, Tblorder.class);
            return orders;
        } catch (Exception e) {
            Utility.ShowError(e);
            return orders;
        }
    }

    @Transactional
    public static double rate() {
        CExchange xchange = new CExchange();
        double rate = xchange.getRate(SystemConfig.getCompCurrency());
        return rate;
    }

    @Transactional
    public static CCurrency compCur() {
        return SystemConfig.getCompCurrency();
    }

    public static double equiv(double amount) {
        CExchange ex = new CExchange();
        double eq = ex.convert(SystemConfig.getDefaultCurrencyId(), SystemConfig.getCompCurrency().getId(), amount);
        return Math.round(eq);
    }

    public static String prop(String key) {
        return Utility.getProperty(key);
    }
}

package cqf.core;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public enum Basis {
    Act,            // Act/Act 	Actual/Actual, Actual/Actual (ISDA)
    Act365F,        // Act/365F 	Actual/365 Fixed, English
    Act360,         // Act/360 	Actual/360 , French
    Act365A,        // Act/365A 	Actual/365 Actual
    Act365L,        // Act/365L 	Actual/365 Leap year
    NL365,          // NL/365 	Actual/365 No leap year , NL365
    ISDA30360,      // 30/360 ISDA 	30/360 U.S. Municipal, Bond basis
    ISMA30E360,     // 30E/360 	30/360 ISMA, 30/360 European, 30S/360 Special German, Eurobond Basis
    EPlus30360,     // 30E+/360 	30E+/360
    German30360,    // 30/360 German 	30E/360 ISDA
    US30360         // 30/360 US 	30U/360,30US/360
};

/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2018.
 */

package ch.sbb.matsim.config;

import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ReflectiveConfigGroup;
import org.matsim.core.utils.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mrieser / SBB
 */
public class SwissRailRaptorConfigGroup extends ReflectiveConfigGroup {

    public static final String GROUP = "swissRailRaptor";

    private static final String PARAM_USE_RANGE_QUERY = "useRangeQuery";
    private static final String PARAM_USE_INTERMODAL_ACCESS_EGRESS = "useIntermodalAccessEgress";
    private static final String PARAM_USE_MODE_MAPPING = "useModeMappingForPassengers";
    private static final String PARAM_TRANSFER_PENALTY_FACTOR = "transferPenaltyTravelTimeToCostFactor";

    private boolean useRangeQuery = false;
    private boolean useIntermodality = false;
    private boolean useModeMapping = false;

    private double transferPenaltyTravelTimeToCostFactor = 0.0;

    private final Map<String, RangeQuerySettingsParameterSet> rangeQuerySettingsPerSubpop = new HashMap<>();
    private final List<IntermodalAccessEgressParameterSet> intermodalAccessEgressSettings = new ArrayList<>();
    private final Map<String, ModeMappingForPassengersParameterSet> modeMappingForPassengersByRouteMode = new HashMap<>();

    public SwissRailRaptorConfigGroup() {
        super(GROUP);
    }

    @StringGetter(PARAM_USE_RANGE_QUERY)
    public boolean isUseRangeQuery() {
        return this.useRangeQuery;
    }

    @StringSetter(PARAM_USE_RANGE_QUERY)
    public void setUseRangeQuery(boolean useRangeQuery) {
        this.useRangeQuery = useRangeQuery;
    }

    @StringGetter(PARAM_USE_INTERMODAL_ACCESS_EGRESS)
    public boolean isUseIntermodalAccessEgress() {
        return this.useIntermodality;
    }

    @StringSetter(PARAM_USE_INTERMODAL_ACCESS_EGRESS)
    public void setUseIntermodalAccessEgress(boolean useIntermodality) {
        this.useIntermodality = useIntermodality;
    }

    @StringGetter(PARAM_USE_MODE_MAPPING)
    public boolean isUseModeMappingForPassengers() {
        return this.useModeMapping;
    }

    @StringSetter(PARAM_USE_MODE_MAPPING)
    public void setUseModeMappingForPassengers(boolean useModeMapping) {
        this.useModeMapping = useModeMapping;
    }

    @StringGetter(PARAM_TRANSFER_PENALTY_FACTOR)
    public double getTransferPenaltyTravelTimeToCostFactor() {
        return transferPenaltyTravelTimeToCostFactor;
    }

    @StringSetter(PARAM_TRANSFER_PENALTY_FACTOR)
    public void setTransferPenaltyTravelTimeToCostFactor(double transferPenaltyTravelTimeToCostFactor) {
        this.transferPenaltyTravelTimeToCostFactor = transferPenaltyTravelTimeToCostFactor;
    }

    @Override
    public ConfigGroup createParameterSet(String type) {
        if (RangeQuerySettingsParameterSet.TYPE.equals(type)) {
            return new RangeQuerySettingsParameterSet();
        } else if (IntermodalAccessEgressParameterSet.TYPE.equals(type)) {
            return new IntermodalAccessEgressParameterSet();
        } else if (ModeMappingForPassengersParameterSet.TYPE.equals(type)) {
            return new ModeMappingForPassengersParameterSet();
        } else {
            throw new IllegalArgumentException("Unsupported parameterset-type: " + type);
        }
    }

    @Override
    public void addParameterSet(ConfigGroup set) {
        if (set instanceof RangeQuerySettingsParameterSet) {
            addRangeQuerySettings((RangeQuerySettingsParameterSet) set);
        } else if (set instanceof IntermodalAccessEgressParameterSet) {
            addIntermodalAccessEgress((IntermodalAccessEgressParameterSet) set);
        } else if (set instanceof ModeMappingForPassengersParameterSet) {
            addModeMappingForPassengers((ModeMappingForPassengersParameterSet) set);
        } else {
            throw new IllegalArgumentException("Unsupported parameterset: " + set.getClass().getName());
        }
    }

    public void addRangeQuerySettings(RangeQuerySettingsParameterSet settings) {
        Set<String> subpops = settings.getSubpopulations();
        if (subpops.isEmpty()) {
            this.rangeQuerySettingsPerSubpop.put(null, settings);
        } else {
            for (String subpop : subpops) {
                this.rangeQuerySettingsPerSubpop.put(subpop, settings);
            }
        }
        super.addParameterSet(settings);
    }

    public RangeQuerySettingsParameterSet getRangeQuerySettings(String subpopulation) {
        return this.rangeQuerySettingsPerSubpop.get(subpopulation);
    }

    public RangeQuerySettingsParameterSet removeRangeQuerySettings(String subpopulation) {
        RangeQuerySettingsParameterSet paramSet = this.rangeQuerySettingsPerSubpop.remove(subpopulation);
        super.removeParameterSet(paramSet);
        return paramSet;
    }

    public void addIntermodalAccessEgress(IntermodalAccessEgressParameterSet paramSet) {
        this.intermodalAccessEgressSettings.add(paramSet);
        super.addParameterSet(paramSet);
    }

    public List<IntermodalAccessEgressParameterSet> getIntermodalAccessEgressParameterSets() {
        return this.intermodalAccessEgressSettings;
    }

    public void addModeMappingForPassengers(ModeMappingForPassengersParameterSet paramSet) {
        this.modeMappingForPassengersByRouteMode.put(paramSet.getRouteMode(), paramSet);
        super.addParameterSet(paramSet);
    }

    public ModeMappingForPassengersParameterSet getModeMappingForPassengersParameterSet(String routeMode) {
        return this.modeMappingForPassengersByRouteMode.get(routeMode);
    }

    public Collection<ModeMappingForPassengersParameterSet> getModeMappingForPassengers() {
        return this.modeMappingForPassengersByRouteMode.values();
    }

    public static class RangeQuerySettingsParameterSet extends ReflectiveConfigGroup {

        private static final String TYPE = "rangeQuerySettings";

        private static final String PARAM_SUBPOPS = "subpopulations";
        private static final String PARAM_MAX_EARLIER_DEPARTURE = "maxEarlierDeparture_sec";
        private static final String PARAM_MAX_LATER_DEPARTURE = "maxLaterDeparture_sec";

        private final Set<String> subpopulations = new HashSet<>();
        private int maxEarlierDeparture = 600;
        private int maxLaterDeparture = 900;

        public RangeQuerySettingsParameterSet() {
            super(TYPE);
        }

        @StringGetter(PARAM_SUBPOPS)
        public String getSubpopulationsAsString() {
            return CollectionUtils.setToString(this.subpopulations);
        }

        public Set<String> getSubpopulations() {
            return this.subpopulations;
        }

        @StringSetter(PARAM_SUBPOPS)
        public void setSubpopulations(String subpopulation) {
            this.setSubpopulations(CollectionUtils.stringToSet(subpopulation));
        }

        public void setSubpopulations(Set<String> subpopulations) {
            this.subpopulations.clear();
            this.subpopulations.addAll(subpopulations);
        }

        @StringGetter(PARAM_MAX_EARLIER_DEPARTURE)
        public int getMaxEarlierDeparture() {
            return maxEarlierDeparture;
        }

        @StringSetter(PARAM_MAX_EARLIER_DEPARTURE)
        public void setMaxEarlierDeparture(int maxEarlierDeparture) {
            this.maxEarlierDeparture = maxEarlierDeparture;
        }

        @StringGetter(PARAM_MAX_LATER_DEPARTURE)
        public int getMaxLaterDeparture() {
            return maxLaterDeparture;
        }

        @StringSetter(PARAM_MAX_LATER_DEPARTURE)
        public void setMaxLaterDeparture(int maxLaterDeparture) {
            this.maxLaterDeparture = maxLaterDeparture;
        }
    }

    public static class IntermodalAccessEgressParameterSet extends ReflectiveConfigGroup {

        private static final String TYPE = "intermodalAccessEgress";

        private static final String PARAM_SUBPOPS = "subpopulations";
        private static final String PARAM_MODE = "mode";
        private static final String PARAM_RADIUS = "radius";
        private static final String PARAM_LINKID_ATTRIBUTE = "linkIdAttribute";
        private static final String PARAM_FILTER_ATTRIBUTE = "filterAttribute";
        private static final String PARAM_FILTER_VALUE = "filterValue";

        private final Set<String> subpopulations = new HashSet<>();
        private String mode;
        private double radius;
        private String linkIdAttribute;
        private String filterAttribute;
        private String filterValue;

        public IntermodalAccessEgressParameterSet() {
            super(TYPE);
        }

        @StringGetter(PARAM_SUBPOPS)
        public String getSubpopulationsAsString() {
            return CollectionUtils.setToString(this.subpopulations);
        }

        public Set<String> getSubpopulations() {
            return this.subpopulations;
        }

        @StringSetter(PARAM_SUBPOPS)
        public void setSubpopulations(String subpopulations) {
            this.setSubpopulations(CollectionUtils.stringToSet(subpopulations));
        }

        public void setSubpopulations(Set subpopulations) {
            this.subpopulations.clear();
            this.subpopulations.addAll(subpopulations);
        }

        @StringGetter(PARAM_MODE)
        public String getMode() {
            return mode;
        }

        @StringSetter(PARAM_MODE)
        public void setMode(String mode) {
            this.mode = mode;
        }

        @StringGetter(PARAM_RADIUS)
        public double getRadius() {
            return radius;
        }

        @StringSetter(PARAM_RADIUS)
        public void setRadius(double radius) {
            this.radius = radius;
        }

        @StringGetter(PARAM_LINKID_ATTRIBUTE)
        public String getLinkIdAttribute() {
            return linkIdAttribute;
        }

        @StringSetter(PARAM_LINKID_ATTRIBUTE)
        public void setLinkIdAttribute(String linkIdAttribute) {
            this.linkIdAttribute = linkIdAttribute;
        }

        @StringGetter(PARAM_FILTER_ATTRIBUTE)
        public String getFilterAttribute() {
            return filterAttribute;
        }

        @StringSetter(PARAM_FILTER_ATTRIBUTE)
        public void setFilterAttribute(String filterAttribute) {
            this.filterAttribute = filterAttribute;
        }

        @StringGetter(PARAM_FILTER_VALUE)
        public String getFilterValue() {
            return filterValue;
        }

        @StringSetter(PARAM_FILTER_VALUE)
        public void setFilterValue(String filterValue) {
            this.filterValue = filterValue;
        }

        @Override
        public Map<String, String> getComments() {
            Map<String, String> map = super.getComments();
            map.put(PARAM_SUBPOPS, "Comma-separated list of names of subpopulations to which this mode is available. Leaving it empty applies to all agents.");
            map.put(PARAM_FILTER_ATTRIBUTE, "Name of the transit stop attribute used to filter stops that should be included in the set of potential stops for access and egress. The attribute should be of type String. 'null' disables the filter and all stops within the specified radius will be used.");
            map.put(PARAM_FILTER_VALUE, "Only stops where the filter attribute has the value specified here will be considered as access or egress stops.");
            map.put(PARAM_LINKID_ATTRIBUTE, "If the mode is routed on the network, specify which linkId acts as access link to this stop in the transport modes sub-network.");
            return map;
        }
    }

    public static class ModeMappingForPassengersParameterSet extends ReflectiveConfigGroup {

        private static final String TYPE = "modeMapping";

        private static final String PARAM_ROUTE_MODE = "routeMode";
        private static final String PARAM_PASSENGER_MODE = "passengerMode";

        private String routeMode = null;
        private String passengerMode = null;

        public ModeMappingForPassengersParameterSet() {
            super(TYPE);
        }

        public ModeMappingForPassengersParameterSet(String routeMode, String passengerMode) {
            super(TYPE);
            this.routeMode = routeMode;
            this.passengerMode = passengerMode;
        }

        @StringGetter(PARAM_ROUTE_MODE)
        public String getRouteMode() {
            return routeMode;
        }

        @StringSetter(PARAM_ROUTE_MODE)
        public void setRouteMode(String routeMode) {
            this.routeMode = routeMode;
        }

        @StringGetter(PARAM_PASSENGER_MODE)
        public String getPassengerMode() {
            return passengerMode;
        }

        @StringSetter(PARAM_PASSENGER_MODE)
        public void setPassengerMode(String passengerMode) {
            this.passengerMode = passengerMode;
        }
    }
}
package com.kgs.homeslot.module.property.config;

import com.kgs.homeslot.module.property.entity.Property;
import com.kgs.homeslot.module.property.repository.PropertyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PropertyDataInitializer implements CommandLineRunner {

    private final PropertyRepository propertyRepository;

    public PropertyDataInitializer(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public void run(String... args) {
        if (propertyRepository.count() == 0) {
            List<Property> seedProperties = List.of(
                    Property.builder()
                            .title("Grand Horizon Luxury Apartments")
                            .description("Spacious 3 BHK luxury apartment with scenic city views, high-end marble flooring, and smart home automation.")
                            .propertyType("APARTMENT")
                            .listingType("BUY")
                            .price(new BigDecimal("12500000.00")) // 1.25 Cr
                            .bhk(3)
                            .bathrooms(3)
                            .areaSqft(1850.0)
                            .address("Banjara Hills, Road No. 12")
                            .city("Hyderabad")
                            .state("Telangana")
                            .zipCode("500034")
                            .latitude(17.4156)
                            .longitude(78.4347)
                            .status("READY_TO_MOVE")
                            .amenities("Swimming Pool, Gym, 24/7 Security, Covered Parking, Clubhouse, Power Backup, Elevator")
                            .coverImageUrl("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80")
                            .imageUrls("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80")
                            .builderName("Prestige Group")
                            .avgRating(4.8)
                            .reviewCount(14)
                            .build(),

                    Property.builder()
                            .title("Emerald Heights Villas")
                            .description("Exclusive 4 BHK gated villa surrounded by lush greenery, private swimming pool, and private garden.")
                            .propertyType("VILLA")
                            .listingType("BUY")
                            .price(new BigDecimal("35000000.00")) // 3.5 Cr
                            .bhk(4)
                            .bathrooms(4)
                            .areaSqft(3600.0)
                            .address("Whitefield, ITPL Main Road")
                            .city("Bangalore")
                            .state("Karnataka")
                            .zipCode("560066")
                            .latitude(12.9698)
                            .longitude(77.7499)
                            .status("READY_TO_MOVE")
                            .amenities("Private Pool, Garden, Solar Energy, Smart Security, Tennis Court, Clubhouse")
                            .coverImageUrl("https://images.unsplash.com/photo-1613977257363-707ba9348227?auto=format&fit=crop&w=800&q=80")
                            .imageUrls("https://images.unsplash.com/photo-1613977257363-707ba9348227?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=800&q=80")
                            .builderName("Sobha Developers")
                            .avgRating(4.9)
                            .reviewCount(22)
                            .build(),

                    Property.builder()
                            .title("Skyline Towers Penthouse")
                            .description("Ultra-premium 4 BHK penthouse with 360-degree panoramic skyline views and private terrace Jacuzzi.")
                            .propertyType("PENTHOUSE")
                            .listingType("BUY")
                            .price(new BigDecimal("48000000.00")) // 4.8 Cr
                            .bhk(4)
                            .bathrooms(5)
                            .areaSqft(4200.0)
                            .address("Worli Sea Face")
                            .city("Mumbai")
                            .state("Maharashtra")
                            .zipCode("400018")
                            .latitude(19.0176)
                            .longitude(72.8172)
                            .status("UNDER_CONSTRUCTION")
                            .amenities("Infinity Pool, Private Elevator, Concierge Service, Gym, Helipad Access, Smart Home")
                            .coverImageUrl("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80")
                            .imageUrls("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b?auto=format&fit=crop&w=800&q=80")
                            .builderName("Lodha Group")
                            .avgRating(4.7)
                            .reviewCount(9)
                            .build(),

                    Property.builder()
                            .title("Urban Nest Modern 2BHK")
                            .description("Compact and stylish 2 BHK flat near tech parks, ideal for working professionals and small families.")
                            .propertyType("APARTMENT")
                            .listingType("BUY")
                            .price(new BigDecimal("6800000.00")) // 68 Lakhs
                            .bhk(2)
                            .bathrooms(2)
                            .areaSqft(1150.0)
                            .address("HITEC City, Cyberabad")
                            .city("Hyderabad")
                            .state("Telangana")
                            .zipCode("500081")
                            .latitude(17.4435)
                            .longitude(78.3772)
                            .status("READY_TO_MOVE")
                            .amenities("Gym, EV Charging, Children Play Area, Intercom, Power Backup, 24/7 Water Supply")
                            .coverImageUrl("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80")
                            .imageUrls("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80")
                            .builderName("My Home Constructions")
                            .avgRating(4.5)
                            .reviewCount(18)
                            .build(),

                    Property.builder()
                            .title("Greenwood Meadows Villa")
                            .description("Serene 3 BHK duplex villa in a eco-friendly gated township with organic farm access.")
                            .propertyType("VILLA")
                            .listingType("BUY")
                            .price(new BigDecimal("19500000.00")) // 1.95 Cr
                            .bhk(3)
                            .bathrooms(3)
                            .areaSqft(2400.0)
                            .address("Gachibowli Financial District")
                            .city("Hyderabad")
                            .state("Telangana")
                            .zipCode("500032")
                            .latitude(17.4401)
                            .longitude(78.3489)
                            .status("UNDER_CONSTRUCTION")
                            .amenities("Clubhouse, Jogging Track, Rainwater Harvesting, Amphitheatre, Security, Gym")
                            .coverImageUrl("https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80")
                            .imageUrls("https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80")
                            .builderName("Aparna Enterprises")
                            .avgRating(4.6)
                            .reviewCount(11)
                            .build(),

                    Property.builder()
                            .title("Palm Breeze Luxury 3BHK")
                            .description("Sea-facing 3 BHK rental luxury residence with wrap-around balconies and premium furnishings.")
                            .propertyType("APARTMENT")
                            .listingType("RENT")
                            .price(new BigDecimal("75000.00")) // 75k per month
                            .bhk(3)
                            .bathrooms(3)
                            .areaSqft(1650.0)
                            .address("Marine Drive")
                            .city("Kochi")
                            .state("Kerala")
                            .zipCode("682031")
                            .latitude(9.9790)
                            .longitude(76.2764)
                            .status("READY_TO_MOVE")
                            .amenities("Infinity Pool, Covered Parking, Gym, Sea View, Security, High-Speed Elevators")
                            .coverImageUrl("https://images.unsplash.com/photo-1567496898669-ee935f5f647a?auto=format&fit=crop&w=800&q=80")
                            .imageUrls("https://images.unsplash.com/photo-1567496898669-ee935f5f647a?auto=format&fit=crop&w=800&q=80")
                            .builderName("Asset Homes")
                            .avgRating(4.7)
                            .reviewCount(8)
                            .build()
            );

            propertyRepository.saveAll(seedProperties);
            System.out.println("PropertyDataInitializer: Populated " + seedProperties.size() + " demo property listings successfully.");
        }
    }
}

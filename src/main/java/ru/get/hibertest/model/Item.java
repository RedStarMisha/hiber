package ru.get.hibertest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.*;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meSeqGen")
    @SequenceGenerator(name = "mySeqGen", sequenceName = "my_sequence_name")
    private Integer id;
    @NotBlank(message = "Name should not be null")
    @NotBlank
    @Size(min = 2, max = 255, message = "Name size should from 2 to 255 char")
    @Column(nullable = false)
    private String name;
    private String description;
    @Formula("substr(DESCRIPTION, 1, 12)")
    private String shortDescription;

    @Column(name = "IMPERIALWEIGHT")
    @ColumnTransformer(
            read = "IMPERIALWEIGHT / 2.20465",
            write = "? * 2.20462")
    private double metricWeight;
    private Date createdOn;
    private Boolean verified;
    private AuctionType auctionType;
    @Column(insertable = false)
    @ColumnDefault("1.00")
    @Generated(event = EventType.INSERT)
    private BigDecimal initialPrice;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false)
    @Generated(event = {EventType.UPDATE, EventType.INSERT})
    private Date lastModified;
    private Date auctionStart;
    private Date auctionEnd;
    private List<Bid> bids = new ArrayList<>();
    @Transient
    private BigDecimal initialPriceIncludeTax;
    public void addBid(Bid bid) {
        if (bid == null) {
            throw new NullPointerException("Bid is null");
        }
        if (bid.getItem() != null) {
            throw new IllegalStateException("Bid already assigned to an Item");
        }
        bids.add(bid);
        bid.setItem(this);
    }
}

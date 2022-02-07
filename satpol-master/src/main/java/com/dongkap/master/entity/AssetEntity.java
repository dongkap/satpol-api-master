package com.dongkap.master.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.dongkap.common.utils.SchemaDatabase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false, exclude={"condition", "businessPartner", "corporate"})
@ToString(exclude={"condition", "businessPartner", "corporate"})
@Entity
@Table(name = "mst_asset", schema = SchemaDatabase.MASTER)
public class AssetEntity extends BaseAuditEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1932022761237540822L;

	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(name = "asset_uuid", nullable = false, unique = true)
	private String id;

	@Column(name = "asset_name", nullable = false)
	private String assetName;

	@Column(name = "asset_condition", nullable = false)
	private String assetCondition;

	@ManyToOne(targetEntity = ParameterEntity.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "asset_condition", nullable = false, referencedColumnName = "parameter_code", insertable = false, updatable = false)
	private ParameterEntity condition;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "description")
	private String description;

	@ManyToOne(targetEntity = BusinessPartnerEntity.class, fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "bp_uuid", nullable = true)
	private BusinessPartnerEntity businessPartner;

	@ManyToOne(targetEntity = CorporateEntity.class, fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "corporate_uuid", nullable = false)
	private CorporateEntity corporate;

}
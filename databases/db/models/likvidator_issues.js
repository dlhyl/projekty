module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "likvidator_issues",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      bulletin_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "bulletin_issues",
          key: "id",
        },
      },
      raw_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "raw_issues",
          key: "id",
        },
      },
      legal_form_code: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      legal_form_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      corporate_body_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      cin: {
        type: DataTypes.BIGINT,
        allowNull: false,
      },
      sid: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      street: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      building_number: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      city: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      postal_code: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      country: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      in_business_register: {
        type: DataTypes.BOOLEAN,
        allowNull: false,
        defaultValue: false,
      },
      br_insertion: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      br_court_code: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      br_court_name: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      br_section: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      other_registrar_name: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      other_registration_number: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      decision_based_on: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      decision_date: {
        type: DataTypes.DATEONLY,
        allowNull: false,
      },
      claim_term: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      liquidation_start_date: {
        type: DataTypes.DATEONLY,
        allowNull: false,
      },
      created_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
      updated_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
      debtee_legal_form_code: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      debtee_legal_form_name: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      company_id: {
        type: DataTypes.BIGINT,
        allowNull: true,
        references: {
          model: "companies",
          key: "cin",
        },
      },
    },
    {
      sequelize,
      tableName: "likvidator_issues",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.likvidator_issues_on_bulletin_issue_id",
          fields: [{ name: "bulletin_issue_id" }],
        },
        {
          name: "index_ov.likvidator_issues_on_raw_issue_id",
          unique: true,
          fields: [{ name: "raw_issue_id" }],
        },
        {
          name: "index_ov.likvidator_issues_on_updated_at_and_id",
          unique: true,
          fields: [{ name: "updated_at" }, { name: "id" }],
        },
        {
          name: "likvidator_issues_company_id",
          fields: [{ name: "company_id" }],
        },
        {
          name: "likvidator_issues_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};

module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "konkurz_vyrovnanie_issues",
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
      court_code: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      court_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      file_reference: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      corporate_body_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      cin: {
        type: DataTypes.BIGINT,
        allowNull: true,
      },
      street: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      building_number: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      city: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      postal_code: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      country: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      kind_code: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      kind_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      announcement: {
        type: DataTypes.TEXT,
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
      tableName: "konkurz_vyrovnanie_issues",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.konkurz_vyrovnanie_issues_on_bulletin_issue_id",
          fields: [{ name: "bulletin_issue_id" }],
        },
        {
          name: "index_ov.konkurz_vyrovnanie_issues_on_raw_issue_id",
          unique: true,
          fields: [{ name: "raw_issue_id" }],
        },
        {
          name: "index_ov.konkurz_vyrovnanie_issues_on_updated_at_and_id",
          unique: true,
          fields: [{ name: "updated_at" }, { name: "id" }],
        },
        {
          name: "konkurz_vyrovnanie_issues_company_id",
          fields: [{ name: "company_id" }],
        },
        {
          name: "konkurz_vyrovnanie_issues_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};

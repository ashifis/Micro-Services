import React, { useState, useEffect } from "react";
import "../csss/TeamStatistics.scss";
import { useParams, Link } from "react-router-dom";
import { PieChart } from "react-minimal-pie-chart";
import TeamImage from "../components/TeamImage";

export const TeamStatistics = ({ team }) => {
  const { rootTeamName } = useParams();
  const rootTeamRoute = "/teams/" + rootTeamName;
  const [stat, setStat] = useState({ statistics: [] });

  useEffect(() => {
    const fetchTeamStatistics = async () => {
      const response = await fetch(
        `${process.env.REACT_APP_API_ROOT_URL}/teams/${rootTeamName}/statistics`
      );
      const data = await response.json();
      setStat(data);
    };
    fetchTeamStatistics();
  }, [rootTeamName]);

  if (!stat || stat.statistics.length === 0) {
    return (
      <div>
        <div className="TeamStatistics">
          <div className="team-name-section">
            <h1 className="team-name"> {rootTeamName}</h1>

            <Link to={rootTeamRoute}> Latest Matches</Link>
          </div>
          <div className="team-image">
            <TeamImage team={rootTeamName} />
          </div>
        </div>
        <div className="pnf-section">
          <h1>Oops... Page Not Found!</h1>
        </div>
      </div>
    );
  }

  return (
    <React.Fragment>
      <div className="TeamStatistics">
        <div className="team-name-section">
          <h1 className="team-name"> {rootTeamName}</h1>

          <Link to={rootTeamRoute}> Latest Matches</Link>
        </div>
        <div className="team-image">
          <TeamImage team={rootTeamName} />
        </div>
        {stat.statistics.map((st) => (
          <div
            className="win-vs-loss-section stat-teams-section-card"
            key={st.opponent}
          >
            <div className="win-loss-count-section">
              <h4>vs</h4>
              <Link to={"/teams/" + st.opponent + "/statistics"}>
                {st.opponent}
              </Link>
              <h4>Win</h4>
              <p>{st.won}</p>
              <h4>Loss</h4>
              <p>{st.lost}</p>
            </div>
            <div>
              <div className="pie-chart-section">
                <PieChart
                  data={[
                    {
                      title: "Won",
                      value: st.won,
                      color: "#4da375",
                    },
                    { title: "Lost", value: st.lost, color: "#a34d5d" },
                  ]}
                />
              </div>
            </div>
          </div>
        ))}
      </div>
    </React.Fragment>
  );
};

export default TeamStatistics;

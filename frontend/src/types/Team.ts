export type TeamMember = {
  id: string;
  name: string;
};

export type TeamProfile = {
  teamName: string;
  contactNumber: string;
  email: string;
  members: TeamMember[];
};

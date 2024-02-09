export function hashCode(s: string): number {
    let hash = 0;
    for (let i = 0; i < s.length; i++) {
        hash = s.charCodeAt(i) + ((hash << 5) - hash);
    }
    return hash;
}

export function formatPastDate(date: Date): string {
    const now = new Date();
    const diff = Math.abs(now.getTime() - date.getTime()) / 1000;
    if (diff < 60) { // Less than a minute
        return "just now";
    } else if (diff < 120) { // Less than two minutes
        return "a minute ago";
    } else if (diff < 3600) { // Less than an hour
        return `${Math.floor(diff / 60).toString(10)} minutes ago`;
    } else if (diff < 7200) { // Less than two hours
        return "an hour ago";
    } else if (diff < 86400) { // Less than a day (24 hours)
        return `${Math.floor(diff / 3600).toString(10)} hours ago`;
    } else if (diff < 172800) { // Less than two days
        return "a day ago";
    } else if (diff < 604800) { // Less than a week (7 days)
        return `${Math.floor(diff / 86400).toString(10)} days ago`;
    } else if (diff < 1209600) { // Less than two weeks
        return "a week ago";
    } else if (diff < 2592000) { // Less than a month (30 days)
        return `${Math.floor(diff / 604800).toString(10)} weeks ago`;
    } else if (diff < 5184000) { // Less than two months
        return "a month ago";
    } else if (diff < 31536000) { // Less than a year (365 days)
        return `${Math.floor(diff / 2592000).toString(10)} months ago`;
    } else if (diff < 63072000) { // Less than two years
        return "a year ago";
    } else {
        return `${Math.floor(diff / 31536000).toString(10)} years ago`;
    }
}